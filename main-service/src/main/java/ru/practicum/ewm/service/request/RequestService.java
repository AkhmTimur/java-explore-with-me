package ru.practicum.ewm.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.request.RequestRepository;
import ru.practicum.ewm.util.EventState;
import ru.practicum.ewm.util.RequestStatus;
import ru.practicum.ewm.validator.EntityValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.RequestMapper.requestToDto;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final EntityValidator entityValidator;

    @Transactional
    public ParticipationRequestDto addRequest(Long eventId, Long userId) {
        User user = entityValidator.getUserIfExist(userId);
        Event event = entityValidator.getEventIfExist(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().withNano(0))) {
            throw new DataConflictException("Event date must be in the future");
        }
        if (event.getInitiator().equals(user)) {
            throw new DataConflictException("Request couldn't be created by initiator");
        }
        Optional<ParticipationRequest> request = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (request.isPresent()) {
            throw new DataConflictException("Request already exists");
        }
        if (!event.getState().equals((EventState.PUBLISHED))) {
            throw new DataConflictException("Couldn't created request for not published event");
        }
        List<ParticipationRequest> requests = findConfirmedRequests(List.of(event));
        if (requests.size() >= event.getParticipantLimit()) {
            throw new DataConflictException("Reached event's limit of requests");
        }

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now().withNano(0))
                .event(event)
                .requester(user)
                .build();

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            participationRequest.setStatus(RequestStatus.PENDING);
        }
        return requestToDto(requestRepository.save(participationRequest));
    }

    public List<ParticipationRequest> findConfirmedRequests(List<Event> events) {
        List<ParticipationRequest> result = requestRepository.findAllByEventIn(events);
        return result.stream().filter(r -> r.getStatus().equals(RequestStatus.CONFIRMED)).collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long requestId, Long userId) {
        User user = entityValidator.getUserIfExist(userId);
        ParticipationRequest request = entityValidator.getRequestIfExist(requestId);
        if (!user.equals(request.getRequester())) {
            throw new DataConflictException("Only requester can cancel request");
        }
        request.setStatus(RequestStatus.CANCELED);
        return requestToDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> allUserRequests(Long userId) {
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(RequestMapper::requestToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequest(Long eventId, Long userId) {
        Event event = entityValidator.getEventIfExist(eventId);
        User user = entityValidator.getUserIfExist(userId);
        if (!user.equals(event.getInitiator())) {
            throw new DataConflictException("User isn't initiator");
        }
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMapper::requestToDto).collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult update(Long eventId, Long userId, EventRequestStatusUpdateRequest request) {
        Event event = entityValidator.getEventIfExist(eventId);
        User user = entityValidator.getUserIfExist(userId);
        if (!user.equals(event.getInitiator())) {
            throw new DataConflictException("User isn't initiator");
        }
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new DataConflictException("There is no more confirms for this event");
        }
        int confirmedRequests = findConfirmedRequests(List.of(event)).size();
        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(request.getRequestIds());
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        List<ParticipationRequestDto> requestDtos;
        switch (request.getStatus()) {
            case CONFIRMED:
                for (ParticipationRequest r : requests) {
                    if (!r.getStatus().equals(RequestStatus.PENDING)) {
                        throw new DataConflictException("Status must be equals pending");
                    }
                    if (confirmedRequests == event.getParticipantLimit()) {
                        throw new DataConflictException("Reached participant limit");
                    }
                    r.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests++;
                }
                requestDtos = requestRepository.saveAll(requests).stream()
                        .map(RequestMapper::requestToDto).collect(Collectors.toList());
                updateResult.setConfirmedRequests(requestDtos);
                break;
            case REJECTED:
                for (ParticipationRequest r : requests) {
                    if (!r.getStatus().equals(RequestStatus.PENDING)) {
                        throw new DataConflictException("Status must be equals pending");
                    }
                    r.setStatus(RequestStatus.REJECTED);
                }
                requestDtos = requestRepository.saveAll(requests).stream()
                        .map(RequestMapper::requestToDto).collect(Collectors.toList());
                updateResult.setRejectedRequests(requestDtos);
                break;
        }
        return updateResult;
    }

}

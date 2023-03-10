package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.UpdateEventRequest;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.event.EventRepository;
import ru.practicum.ewm.service.request.RequestService;
import ru.practicum.ewm.util.EventState;
import ru.practicum.ewm.validator.EntityValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.practicum.ewm.mapper.EventMapper.eventToEventFullDto;
import static ru.practicum.ewm.mapper.EventMapper.newEventDtoToEvent;

@Service
@RequiredArgsConstructor
public class EventPrivateService {
    private final EventRepository eventRepository;
    private final EntityValidator entityValidator;
    private final EventCommonService eventCommonService;
    private final RequestService requestService;

    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DataConflictException("Event date should be in the future");
        }
        Category category = entityValidator.getCategoryIfExist(newEventDto.getCategory());
        Event event = newEventDtoToEvent(newEventDto, category);
        event.setCategory(category);
        User initiator = entityValidator.getUserIfExist(userId);
        event.setInitiator(initiator);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        Event saved = eventRepository.save(event);
        return eventToEventFullDto(saved);
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> getUserEvents(Long userId, long from, int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<Event> events = eventRepository
                .findAllByIdIsGreaterThanEqualAndInitiatorIdIs(from, userId, pageRequest);
        List<EventFullDto> eventFullDtos = eventCommonService.setViewsToEvents(events);
        List<ParticipationRequest> confirmedRequests = requestService.findConfirmedRequests(events);
        for (EventFullDto fullDto : eventFullDtos) {
            fullDto.setConfirmedRequests((int) confirmedRequests.stream().filter(r -> r.getEvent().getId().equals(fullDto.getId())).count());
        }
        return eventFullDtos;
    }

    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = entityValidator.getEventIfExist(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NotFoundException("Initiator and user have different ids");
        }
        EventFullDto eventFullDto = eventToEventFullDto(event);
        if (eventFullDto.getState().equals(EventState.PUBLISHED)) {
            Map<Long, Long> views = eventCommonService.getStats(List.of(event), false);
            eventFullDto.setViews(views.get(event.getId()));
            List<ParticipationRequest> confirmedRequests = requestService
                    .findConfirmedRequests(List.of(event));
            eventFullDto.setConfirmedRequests(confirmedRequests.size());
        }
        return eventFullDto;
    }

    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventRequest updateEventUserRequest) {
        Event event = entityValidator.getEventIfExist(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NotFoundException("Only initiator can update event");
        }
        Event updatedEvent = eventCommonService.setUpdateRequestParamToEvent(event, updateEventUserRequest);
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    updatedEvent.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    updatedEvent.setState(EventState.CANCELED);
                    break;
            }
        }
        return eventToEventFullDto(eventRepository.save(updatedEvent));
    }
}

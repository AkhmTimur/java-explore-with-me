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
import ru.practicum.ewm.model.eventLike.Like;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.event.EventRepository;
import ru.practicum.ewm.repository.like.LikeRepository;
import ru.practicum.ewm.service.category.CategoryPublicService;
import ru.practicum.ewm.service.user.UserService;
import ru.practicum.ewm.util.EventState;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.EventMapper.eventToEventFullDto;
import static ru.practicum.ewm.mapper.EventMapper.newEventDtoToEvent;

@Service
@RequiredArgsConstructor
@Transactional
public class EventPrivateService {
    private final EventRepository eventRepository;
    private final EventCommonService eventCommonService;
    private final LikeRepository likeRepository;
    private final CategoryPublicService categoryPublicService;
    private final UserService userService;

    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        LocalDateTime plus2 = LocalDateTime.now().withNano(0).plusHours(2);
        if (newEventDto.getEventDate().isBefore(plus2)) {
            throw new DataConflictException("Event date should be in the future more then 2 hours");
        }
        Category category = categoryPublicService.getCategoryIfExist(newEventDto.getCategory());
        Event event = newEventDtoToEvent(newEventDto, category);
        event.setCategory(category);
        User initiator = userService.getUserIfExist(userId);
        event.setInitiator(initiator);
        event.setState(EventState.PENDING.toString());
        event.setCreatedOn(LocalDateTime.now().withNano(0));
        Event saved = eventRepository.save(event);
        return eventToEventFullDto(saved);
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> getUserEvents(Long userId, Integer from, Integer size, Boolean isRating) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        List<EventFullDto> eventFullDtos = eventCommonService.setViewsToEvents(events);
        eventCommonService.setLikesToEventDtos(events, eventFullDtos);
        if (isRating) {
            return eventFullDtos.stream()
                    .sorted(Comparator.comparing(EventFullDto::getLikesCount)).collect(Collectors.toList());
        }
        return eventFullDtos;
    }

    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventCommonService.getEventIfExist(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NotFoundException("Initiator and user have different ids");
        }
        eventCommonService.setViewsToEvents(List.of(event));
        return eventToEventFullDto(event);
    }

    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventRequest updateEventUserRequest) {
        Event event = eventCommonService.getEventIfExist(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new DataConflictException("Only initiator can update event");
        }
        if (event.getState().equals(EventState.PUBLISHED.toString())) {
            throw new DataConflictException("Published event can't be updated");
        }
        Event updatedEvent = eventCommonService.setUpdateRequestParamToEvent(event, updateEventUserRequest);
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    updatedEvent.setState(EventState.PENDING.toString());
                    break;
                case CANCEL_REVIEW:
                    updatedEvent.setState(EventState.CANCELED.toString());
                    break;
            }
        }
        return eventToEventFullDto(eventRepository.save(updatedEvent));
    }

    public void addLikeToEvent(Long userId, Long eventId) {
        Event event = eventCommonService.getEventIfExist(eventId);
        User user = userService.getUserIfExist(userId);
        if (likeRepository.findByEventIsAndUserIs(event, user).isPresent()) {
            throw new DataConflictException("Like to event " + event + " from user " + user + " already exist");
        }
        if (event.getInitiator().getId().equals(user.getId())) {
            throw new DataConflictException("Initiator cannot like hiw own event");
        }
        if (!event.getState().equals(EventState.PUBLISHED.toString())) {
            throw new DataConflictException("Cannot like unpublished events");
        }
        Like like = new Like(event, user, LocalDateTime.now().withNano(0));
        likeRepository.save(like);
    }

    public void dislikeToEvent(Long userId, Long eventId) {
        Event event = eventCommonService.getEventIfExist(eventId);
        User user = userService.getUserIfExist(userId);
        Like like = likeRepository.findByEventIsAndUserIs(event, user)
                .orElseThrow(() -> new NotFoundException("Event " + event + " like from user " + user + " not found"));
        likeRepository.deleteById(like.getId());
    }
}


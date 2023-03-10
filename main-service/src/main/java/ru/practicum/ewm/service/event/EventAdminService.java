package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.EventAdminFindDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.UpdateEventRequest;
import ru.practicum.ewm.repository.event.EventRepository;
import ru.practicum.ewm.util.ActionStatus;
import ru.practicum.ewm.util.EventState;
import ru.practicum.ewm.validator.EntityValidator;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.mapper.EventMapper.eventToEventFullDto;

@Service
@RequiredArgsConstructor
public class EventAdminService {
    private final EventRepository eventRepository;
    private final EntityValidator entityValidator;
    private final EventCommonService eventCommonService;

    @Transactional(readOnly = true)
    public List<EventFullDto> findByAdmin(EventAdminFindDto e) {
        List<Event> events = eventRepository.adminSearch(e.getUsers(), e.getStates(), e.getCategories(),
                e.getRangeStart(), e.getRangeEnd(), e.getFrom(), e.getSize());
        return eventCommonService.setViewsToEvents(events);
    }

    @Transactional
    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventRequest updateEventRequest) {
        Event event = entityValidator.getEventIfExist(eventId);
        if (!event.getState().equals(EventState.PENDING)) {
            throw new DataConflictException("Wrong event state: " + event.getState());
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusSeconds(1))) {
            throw new DataConflictException("Event date should be in the future");
        }
        Event updatedEvent = eventCommonService.setUpdateRequestParamToEvent(event, updateEventRequest);
        Event saved = new Event();
        if (updateEventRequest.getStateAction() != null) {
            if (updateEventRequest.getStateAction().equals(ActionStatus.PUBLISH_EVENT)) {
                updatedEvent.setPublishedOn(LocalDateTime.now());
                updatedEvent.setState(EventState.PUBLISHED);
            } else if (updateEventRequest.getStateAction().equals(ActionStatus.REJECT_EVENT)) {
                updatedEvent.setState(EventState.CANCELED);
            }
            saved = eventRepository.save(updatedEvent);
        }
        return eventToEventFullDto(saved);
    }

}

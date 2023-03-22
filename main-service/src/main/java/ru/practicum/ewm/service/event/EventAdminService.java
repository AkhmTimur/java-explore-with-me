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

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.mapper.EventMapper.eventToEventFullDto;

@Service
@RequiredArgsConstructor
@Transactional
public class EventAdminService {
    private final EventRepository eventRepository;
    private final EventCommonService eventCommonService;

    @Transactional(readOnly = true)
    public List<EventFullDto> findByAdmin(EventAdminFindDto e) {
        List<Event> events = eventRepository.adminSearch(e.getUsers(), e.getStates(), e.getCategories(),
                e.getRangeStart(), e.getRangeEnd(), e.getFrom(), e.getSize());
        return eventCommonService.setViewsToEvents(events);
    }

    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventRequest updateEventRequest) {
        Event event = eventCommonService.getEventIfExist(eventId);
        if (!event.getState().equals(EventState.PENDING.toString())) {
            throw new DataConflictException("Wrong event state: " + event.getState());
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().withNano(0))) {
            throw new DataConflictException("Event date should be in the future");
        }
        Event updatedEvent = eventCommonService.setUpdateRequestParamToEvent(event, updateEventRequest);
        Event saved = new Event();
        if (updateEventRequest.getStateAction() != null) {
            if (updateEventRequest.getStateAction().equals(ActionStatus.PUBLISH_EVENT)) {
                updatedEvent.setPublishedOn(LocalDateTime.now().withNano(0));
                updatedEvent.setState(EventState.PUBLISHED.toString());
            } else if (updateEventRequest.getStateAction().equals(ActionStatus.REJECT_EVENT)) {
                updatedEvent.setState(EventState.CANCELED.toString());
            }
            saved = eventRepository.save(updatedEvent);
        }
        return eventToEventFullDto(saved);
    }

}

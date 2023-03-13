package ru.practicum.ewm.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.UpdateEventRequest;
import ru.practicum.ewm.stats.dto.HitCountDto;
import ru.practicum.ewm.stats.dto.HitInDto;
import ru.practicum.ewm.stats.stats.StatsClient;
import ru.practicum.ewm.util.EventState;
import ru.practicum.ewm.validator.EntityValidator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventCommonService {
    private final StatsClient statsClient;
    private final EntityValidator entityValidator;
    private final LocationMapper locationMapper;

    public Event setUpdateRequestParamToEvent(Event event, UpdateEventRequest updateEventRequest) {
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            Category category = entityValidator.getCategoryIfExist(updateEventRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            if (updateEventRequest.getEventDate().isBefore(LocalDateTime.now().withNano(0))) {
                throw new DataConflictException("Event date should be in the future");
            }
            event.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getLocation() != null) {
            event.setLocation(locationMapper.dtoToLocation(updateEventRequest.getLocation()));
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (event.getState().equals(EventState.PUBLISHED.toString())) {
            throw new DataConflictException("Event with state: " + event.getState() + " cannot be changed");
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
        return event;
    }

    public Map<Long, Long> getStats(List<Event> events, Boolean unique) {
        Map<Long, Long> result = new HashMap<>();
        for (Event event : events) {
            if (event.getPublishedOn() == null) {
                return new HashMap<>();
            }
        }
        Optional<LocalDateTime> start = events.stream().map(Event::getPublishedOn).min(LocalDateTime::compareTo);
        if (start.isEmpty()) {
            return new HashMap<>();
        }
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<String> uris = eventIds.stream().map(id -> "/events/" + id).collect(Collectors.toList());

        ResponseEntity<Object> response = statsClient.getStats(new HitInDto(start.get(), LocalDateTime.now().withNano(0), uris, unique));
        List<HitCountDto> stats;
        ObjectMapper mapper = new ObjectMapper();
        try {
            stats = Arrays.asList(mapper.readValue(mapper.writeValueAsString(response.getBody()), HitCountDto[].class));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        for (Long eventId : eventIds) {
            Long views = 0L;
            Optional<Long> stat = stats.stream()
                    .filter(s -> s.getUri().equals("/events/" + eventId)).map(HitCountDto::getHits).findFirst();
            if (stat.isPresent()) {
                views = stat.get();
            }
            result.put(eventId, views);
        }

        return result;
    }

    public List<EventFullDto> setViewsToEvents(List<Event> events) {
        List<EventFullDto> eventFullDtos = events.stream()
                .map(EventMapper::eventToEventFullDto).collect(Collectors.toList());

        Map<Long, Long> views = getStats(events, false);
        if (!views.isEmpty()) {
            eventFullDtos.forEach(e -> e.setViews(views.get(e.getId())));
        }
        return eventFullDtos;
    }
}

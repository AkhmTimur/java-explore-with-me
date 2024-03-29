package ru.practicum.ewm.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.UpdateEventRequest;
import ru.practicum.ewm.model.eventLike.Like;
import ru.practicum.ewm.repository.event.EventRepository;
import ru.practicum.ewm.repository.like.LikeRepository;
import ru.practicum.ewm.service.category.CategoryPublicService;
import ru.practicum.ewm.stats.dto.HitCountDto;
import ru.practicum.ewm.stats.dto.HitInDto;
import ru.practicum.ewm.stats.stats.StatsClient;
import ru.practicum.ewm.util.EventState;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventCommonService {
    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final CategoryPublicService categoryPublicService;
    private final LikeRepository likeRepository;

    public Event setUpdateRequestParamToEvent(Event event, UpdateEventRequest updateEventRequest) {
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            Category category = categoryPublicService.getCategoryIfExist(updateEventRequest.getCategory());
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
            event.setLocation(updateEventRequest.getLocation());
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
        Map<String, Long> stats;
        ObjectMapper mapper = new ObjectMapper();
        try {
            HitCountDto[] hitCountDtos = mapper.readValue(mapper.writeValueAsString(response.getBody()), HitCountDto[].class);
            stats = Arrays.stream(hitCountDtos).collect(Collectors.toMap(HitCountDto::getUri, HitCountDto::getHits));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        for (Long eventId : eventIds) {
            Long stat = stats.getOrDefault("/events/" + eventId, 0L);
            result.put(eventId, stat);
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

    public Event getEventIfExist(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Event.class.getSimpleName() + " not found"));
    }

    public void setLikesToEventDtos(List<Event> events, List<EventFullDto> eventFullDtos) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Like> likes = likeRepository.findByEventIdIn(eventIds);
        Map<Long, Long> eventLikes = likes.stream().collect(Collectors.groupingBy(l -> l.getEvent().getId(), Collectors.counting()));
        eventFullDtos.forEach(e -> e.setLikesCount(eventLikes.get(e.getId())));
    }
}

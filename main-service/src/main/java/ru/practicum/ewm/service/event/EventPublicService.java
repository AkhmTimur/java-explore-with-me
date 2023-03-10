package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventPublicSearchDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.repository.event.EventRepository;
import ru.practicum.ewm.service.request.RequestService;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.stats.StatsClient;
import ru.practicum.ewm.util.EventState;
import ru.practicum.ewm.util.Sort;
import ru.practicum.ewm.validator.EntityValidator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.EventMapper.eventToEventFullDto;

@Service
@RequiredArgsConstructor
public class EventPublicService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final EntityValidator entityValidator;
    private final EventCommonService eventCommonService;
    private final RequestService requestService;

    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = entityValidator.getEventIfExist(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(event + " not found");
        }
        EventFullDto eventFullDto = eventToEventFullDto(event);
        Map<Long, Long> views = eventCommonService.getStats(List.of(event), false);
        eventFullDto.setViews(views.get(event.getId()));

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        HitDto hitDto = HitDto.builder()
                .app("APP_NAME")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.createHit(hitDto);
        return eventFullDto;
    }


    @Transactional(readOnly = true)
    public List<EventFullDto> search(EventPublicSearchDto ev, String ip) {
        if (ev == null) {
            return Collections.emptyList();
        }
        List<Event> events = eventRepository.publicSearch(ev.getText(), ev.getCategories(), ev.getPaid(),
                ev.getRangeStart(), ev.getRangeEnd(), ev.getFrom(), ev.getSize());
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        List<EventFullDto> eventFullDtos = events.stream()
                .map(EventMapper::eventToEventFullDto).collect(Collectors.toList());
        Map<Long, Long> views = eventCommonService.getStats(events, false);
        eventFullDtos.forEach(e -> e.setViews(views.get(e.getId())));
        List<ParticipationRequest> confirmedRequests = requestService.findConfirmedRequests(events);
        for (EventFullDto eventFullDto : eventFullDtos) {
            eventFullDto.setConfirmedRequests((int) confirmedRequests.stream()
                    .filter(r -> r.getEvent().getId().equals(eventFullDto.getId())).count());
        }
        HitDto hitDto = HitDto.builder().app("APP_NAME").uri("/events").ip(ip).timestamp(LocalDateTime.now()).build();
        statsClient.createHit(hitDto);
        events.forEach(e -> {
            hitDto.setUri("/events/" + e.getId());
            statsClient.createHit(hitDto);
        });
        if (ev.getSort() != null && ev.getSort().equals(Sort.VIEWS)) {
            return eventFullDtos.stream()
                    .sorted(Comparator.comparing(EventFullDto::getViews)).collect(Collectors.toList());
        }
        return eventFullDtos.stream()
                .sorted(Comparator.comparing(EventFullDto::getEventDate)).collect(Collectors.toList());
    }
}

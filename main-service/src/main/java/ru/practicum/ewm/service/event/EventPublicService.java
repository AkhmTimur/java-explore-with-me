package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventSearch;
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
@Transactional(readOnly = true)
public class EventPublicService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final EventCommonService eventCommonService;
    private final RequestService requestService;

    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = eventCommonService.getEventIfExist(eventId);
        if (!event.getState().equals(EventState.PUBLISHED.toString())) {
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
                .timestamp(LocalDateTime.now().withNano(0))
                .build();
        statsClient.createHit(hitDto);
        return eventFullDto;
    }

    public List<EventFullDto> search(EventSearch ev, String ip) {
        if (ev == null) {
            return Collections.emptyList();
        }
        List<Event> events = eventRepository.search(ev);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        List<EventFullDto> eventFullDtos = events.stream().map(EventMapper::eventToEventFullDto).collect(Collectors.toList());
        Map<Long, Long> views = eventCommonService.getStats(events, false);
        eventFullDtos.forEach(e -> e.setViews(views.get(e.getId())));
        eventCommonService.setLikesToEventDtos(events, eventFullDtos);
        List<ParticipationRequest> confirmedRequests = requestService.findConfirmedRequests(events);
        Map<Long, Long> eventRequestsCount = confirmedRequests.stream().collect(Collectors.groupingBy(r -> r.getEvent().getId(), Collectors.counting()));
        eventFullDtos.forEach(e -> e.setConfirmedRequests(eventRequestsCount.get(e.getId())));
        HitDto hitDto = HitDto.builder().app("APP_NAME").uri("/events").ip(ip).timestamp(LocalDateTime.now().withNano(0)).build();
        statsClient.createHit(hitDto);
        if (ev.getSort() != null) {
            if (ev.getSort().equals(Sort.VIEWS)) {
                return eventFullDtos.stream()
                        .sorted(Comparator.comparing(EventFullDto::getViews)).collect(Collectors.toList());
            }
            if (ev.getSort().equals(Sort.RATING)) {
                return eventFullDtos.stream().filter(e -> e.getLikesCount() != null).sorted(Comparator.comparing(EventFullDto::getLikesCount)).collect(Collectors.toList());
            }
            if (ev.getSort().equals(Sort.EVENT_DATE)) {
                return eventFullDtos.stream()
                        .sorted(Comparator.comparing(EventFullDto::getEventDate)).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}

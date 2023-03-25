package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventSearch;
import ru.practicum.ewm.service.event.EventPublicService;
import ru.practicum.ewm.util.Sort;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.util.DateTimePattern.DATE_TIME_PATTERN;

@RestController
@RequestMapping(path = "/events")
@Validated
@RequiredArgsConstructor
public class EventPublicController {
    private final EventPublicService eventPublicService;

    @GetMapping("{id}")
    public ResponseEntity<Object> getEvent(@PathVariable @Positive Long id, HttpServletRequest request) {
        return new ResponseEntity<>(eventPublicService.getEvent(id, request), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> search(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                         @RequestParam(required = false) Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest request) {
        Sort sortType = Sort.EVENT_DATE;
        switch (sort) {
            case "VIEWS":
                sortType = Sort.VIEWS;
                break;
            case "RATING":
                sortType = Sort.RATING;
        }
        EventSearch eventSearch = EventSearch.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sortType)
                .from(from)
                .size(size)
                .build();
        List<EventFullDto> eventFullDtos =
                eventPublicService.search(eventSearch, request.getRemoteAddr());
        return new ResponseEntity<>(eventFullDtos, HttpStatus.OK);
    }
}

package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.service.event.EventPublicService;
import ru.practicum.ewm.util.Sort;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

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
                                         @RequestParam(required = false) Sort sort,
                                         @RequestParam(required = false) @PositiveOrZero Long from,
                                         @RequestParam(required = false) Integer size,
                                         HttpServletRequest request) {
        List<EventFullDto> eventFullDtos =
                eventPublicService.search(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRemoteAddr());
        return new ResponseEntity<>(eventFullDtos, HttpStatus.OK);
    }
}
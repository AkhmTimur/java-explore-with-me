package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventPublicSearchDto;
import ru.practicum.ewm.service.event.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

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
    public ResponseEntity<Object> search(@RequestParam EventPublicSearchDto eventPublicSearchDto, HttpServletRequest request) {
        List<EventFullDto> eventFullDtos =
                eventPublicService.search(eventPublicSearchDto, request.getRemoteAddr());
        return new ResponseEntity<>(eventFullDtos, HttpStatus.OK);
    }
}

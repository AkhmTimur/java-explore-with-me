package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.model.event.UpdateEventRequest;
import ru.practicum.ewm.service.event.EventPrivateService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventPrivateService eventPrivateService;

    @PostMapping
    public ResponseEntity<Object> addEvent(@PathVariable @NotNull Long userId,
                                           @RequestBody @Valid NewEventDto newEventDto) {
        return new ResponseEntity<>(eventPrivateService.addEvent(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Object> getUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return new ResponseEntity<>(eventPrivateService.getUserEvent(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateEvent(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        return new ResponseEntity<>(eventPrivateService.updateEvent(userId, eventId, updateEventRequest), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getUserEvents(@PathVariable Long userId,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero long from,
                                                @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return new ResponseEntity<>(eventPrivateService.getUserEvents(userId, from, size), HttpStatus.OK);
    }
}

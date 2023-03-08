package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.UpdateEventRequest;
import ru.practicum.ewm.service.event.EventAdminService;
import ru.practicum.ewm.util.EventState;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Validated
@RequiredArgsConstructor
public class EventAdminController {
    private final EventAdminService eventAdminService;

    @GetMapping
    public ResponseEntity<Object> findEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                             @RequestParam(name = "states", required = false) List<EventState> states,
                                             @RequestParam(name = "categories", required = false) List<Long> categories,
                                             @RequestParam(name = "rangeStart", required = false) LocalDateTime rangeStart,
                                             @RequestParam(name = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                             @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero long from,
                                             @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return new ResponseEntity<>(eventAdminService
                .findByAdmin(users, states, categories, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateEvent(@PathVariable Long eventId,
                                              @RequestBody @NotNull @Valid UpdateEventRequest updateEventRequest) {
        return new ResponseEntity<>(eventAdminService.adminUpdateEvent(eventId, updateEventRequest), HttpStatus.OK);
    }
}

package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventAdminFindDto;
import ru.practicum.ewm.model.event.UpdateEventRequest;
import ru.practicum.ewm.service.event.EventAdminService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/admin/events")
@Validated
@RequiredArgsConstructor
public class EventAdminController {
    private final EventAdminService eventAdminService;

    @GetMapping
    public ResponseEntity<Object> findEvents(EventAdminFindDto eventAdminFindDto) {
        return new ResponseEntity<>(eventAdminService
                .findByAdmin(eventAdminFindDto), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateEvent(@PathVariable Long eventId,
                                              @RequestBody @NotNull @Valid UpdateEventRequest updateEventRequest) {
        return new ResponseEntity<>(eventAdminService.adminUpdateEvent(eventId, updateEventRequest), HttpStatus.OK);
    }
}

package ru.practicum.ewm.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.HitCountDto;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.server.service.StatServerService;
import ru.practicum.ewm.stats.server.utils.DateTimeFormatPattern;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatServerController {
    private final StatServerService statServerService;

    @PostMapping("/hit")
    public ResponseEntity<HitDto> createHit(@RequestBody HitDto hit) {
        return new ResponseEntity<>(statServerService.createHit(hit), HttpStatus.CREATED) ;
    }

    @GetMapping("/stats")
    public ResponseEntity<List<HitCountDto>> getStats(@RequestParam @DateTimeFormat(pattern = DateTimeFormatPattern.TIME_PATTERN) LocalDateTime start,
                                                      @RequestParam @DateTimeFormat(pattern = DateTimeFormatPattern.TIME_PATTERN) LocalDateTime end,
                                                      @RequestParam(required = false) Optional<List<String>> uris,
                                                      @RequestParam(defaultValue = "false") boolean unique) {
        return uris.map(u -> new ResponseEntity<>(
                statServerService.getStatsWithUris(start, end, u, unique), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(
                        statServerService.getStats(start, end, unique), HttpStatus.OK));

    }
}

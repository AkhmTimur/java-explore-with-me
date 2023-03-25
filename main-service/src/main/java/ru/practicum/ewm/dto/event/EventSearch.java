package ru.practicum.ewm.dto.event;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.util.Sort;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.util.DateTimePattern.DATE_TIME_PATTERN;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventSearch {
    List<Long> users;
    List<String> states;
    private List<Long> categories;
    private String text;
    private Boolean paid;
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private Sort sort;
    @PositiveOrZero
    @Builder.Default
    private int from = 0;
    @Positive
    @Builder.Default
    private int size = 1000;
}

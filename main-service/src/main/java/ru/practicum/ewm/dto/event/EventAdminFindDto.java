package ru.practicum.ewm.dto.event;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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
public class EventAdminFindDto {
    private List<Long> users;
    private List<String> states;
    private List<Long> categories;
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime rangeEnd;
    @PositiveOrZero
    @Builder.Default
    private int from = 0;
    @Positive
    @Builder.Default
    private int size = 10;
}

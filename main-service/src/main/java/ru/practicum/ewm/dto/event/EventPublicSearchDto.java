package ru.practicum.ewm.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.util.Sort;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.util.DateTimePattern.DATE_TIME_PATTERN;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventPublicSearchDto {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private Sort sort;
    @PositiveOrZero
    private Long from;
    private Integer size;
}

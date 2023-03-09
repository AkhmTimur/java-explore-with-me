package ru.practicum.ewm.dto.event;

import lombok.*;
import ru.practicum.ewm.util.EventState;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventAdminFindDto {
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    @PositiveOrZero
    @Builder.Default
    private long from = 0L;
    @Builder.Default
    private int size = 10;
}

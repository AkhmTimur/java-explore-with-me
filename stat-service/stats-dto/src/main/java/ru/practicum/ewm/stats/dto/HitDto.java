package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.stats.utils.DateTimeFormatPattern;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HitDto {
    @NonNull
    private String app;
    @NonNull
    private String uri;
    @NonNull
    private String ip;
    @JsonFormat(pattern = DateTimeFormatPattern.TIME_PATTERN)
    private LocalDateTime timestamp;
}

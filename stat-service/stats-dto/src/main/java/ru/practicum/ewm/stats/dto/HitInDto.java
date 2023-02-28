package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.stats.utils.DateTimeFormatPattern;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HitInDto {
    @JsonFormat(pattern = DateTimeFormatPattern.TIME_PATTERN)
    private LocalDateTime start;
    @JsonFormat(pattern = DateTimeFormatPattern.TIME_PATTERN)
    private LocalDateTime end;
    private List<String> uri;
    private boolean unique;
}

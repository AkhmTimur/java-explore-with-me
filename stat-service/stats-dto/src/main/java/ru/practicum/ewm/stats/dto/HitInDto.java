package ru.practicum.ewm.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HitInDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uri;
    private boolean unique;
}

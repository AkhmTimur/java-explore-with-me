package ru.practicum.ewm.stats.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HitCountDto {
    private String app;
    private String uri;
    private long hits;
}

package ru.practicum.ewm.model.eventLike;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LikesCount {
    private Long eventId;
    private Long likesCount;
}

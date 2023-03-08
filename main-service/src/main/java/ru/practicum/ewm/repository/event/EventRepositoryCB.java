package ru.practicum.ewm.repository.event;

import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.util.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCB {
    public List<Event> publicSearch(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                            LocalDateTime rangeEnd, long from, int size);
    public List<Event> adminSearch(List<Long> users, List<EventState> states, List<Long> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, long from, int size);
}

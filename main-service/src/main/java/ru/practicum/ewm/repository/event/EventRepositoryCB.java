package ru.practicum.ewm.repository.event;

import ru.practicum.ewm.dto.event.EventSearch;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

public interface EventRepositoryCB {

    List<Event> search(EventSearch eventSearch);
}

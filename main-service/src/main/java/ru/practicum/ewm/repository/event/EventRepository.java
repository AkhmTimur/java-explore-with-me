package ru.practicum.ewm.repository.event;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCB {

    List<Event> findAllByIdIsGreaterThanEqualAndInitiatorIdIs(long from, Long userId, PageRequest pageRequest);

    List<Event> findAllByCategoryId(Long categoryId);
}

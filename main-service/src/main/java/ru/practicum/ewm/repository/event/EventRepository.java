package ru.practicum.ewm.repository.event;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCB {
    Optional<Event> findByCategoryId(Long categoryId);

    List<Event> findAllByIdIsGreaterThanEqualAndInitiatorIdIs(long from, Long userId, PageRequest pageRequest);
}

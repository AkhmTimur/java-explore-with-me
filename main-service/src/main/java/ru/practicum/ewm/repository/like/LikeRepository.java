package ru.practicum.ewm.repository.like;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.eventLike.Like;
import ru.practicum.ewm.model.user.User;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByEventIsAndUserIs(Event event, User user);

    List<Like> findByEventIdIn(List<Long> eventIds);
}

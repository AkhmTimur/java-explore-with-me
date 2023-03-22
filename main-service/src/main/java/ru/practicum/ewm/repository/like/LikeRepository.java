package ru.practicum.ewm.repository.like;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.eventLike.Like;
import ru.practicum.ewm.model.eventLike.LikesCount;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByEventIsAndUserIs(Event event, User user);

    @Query("select new ru.practicum.ewm.model.eventLike.LikesCount(l.event.id, count(l.user.id)) from Like l group by l.event.id order by count(l.user.id) desc")
    List<LikesCount> getMostLiked(PageRequest pageRequest);

    @Query("select new ru.practicum.ewm.model.eventLike.LikesCount(l.event.id, count(l.user.id)) from Like as l where l.likedOn between ?1 and ?2 group by l.event order by count(l.user.id) desc")
    List<LikesCount> getMostLikedBetweenDates(PageRequest pageRequest, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.model.eventLike.LikesCount(l.event.id, count(l.user.id)) from Like l where l.event.initiator.id = ?1 group by l.event.id order by count(l.user.id) desc")
    List<LikesCount> getMostLikedEventsCreatedByUser(Long userId, PageRequest pageRequest);
}

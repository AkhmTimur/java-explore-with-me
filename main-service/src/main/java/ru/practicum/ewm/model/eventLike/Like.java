package ru.practicum.ewm.model.eventLike;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_likes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;
    @ManyToOne
    private Event event;
    @ManyToOne
    private User user;
    private LocalDateTime likedOn;

    public Like(Event event, User user, LocalDateTime likedOn) {
        this.event = event;
        this.user = user;
        this.likedOn = likedOn;
    }
}


package ru.practicum.ewm.model.event;

import lombok.*;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.util.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    @ManyToOne(optional = false)
    private Category category;
    private Long confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;
    @ManyToOne
    private User initiator;
    private Boolean paid;
    private int participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventState state;
    private String title;
    private Integer views;
}

package ru.practicum.ewm.stats.server.model;

import lombok.*;
import ru.practicum.ewm.stats.server.mapper.App;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private App app;
    private String uri;
    private String ip;
    @Column(name = "created")
    private LocalDateTime timestamp;
}

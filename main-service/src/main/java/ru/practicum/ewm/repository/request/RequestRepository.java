package ru.practicum.ewm.repository.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.util.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventInAndStatusIs(List<Event> event, RequestStatus status);

    List<ParticipationRequest> findAllByIdIn(List<Long> ids);

    List<ParticipationRequest> findAllByEventIn(List<Event> events);
}

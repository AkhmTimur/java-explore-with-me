package ru.practicum.ewm.repository.event;

import org.springframework.stereotype.Repository;
import ru.practicum.ewm.dto.event.EventSearch;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.eventLike.Like;
import ru.practicum.ewm.util.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class EventRepositoryCBImpl implements EventRepositoryCB {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Event> search(EventSearch ev) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);
        Predicate predicate = cb.conjunction();

        if (ev.getText() != null && !ev.getText().isEmpty()) {
            Predicate annotation = cb.like(cb.lower(event.get("annotation")), "%" + ev.getText().toLowerCase() + "%");
            Predicate description = cb.like(cb.lower(event.get("description")), "%" + ev.getText().toLowerCase() + "%");
            Predicate hasText = cb.or(description, annotation);
            predicate = cb.and(predicate, hasText);
        }

        if (ev.getUsers() != null) {
            Predicate initiators = event.get("initiator").in(ev.getUsers());
            predicate = cb.and(predicate, initiators);
        }
        if (ev.getStates() != null) {
            Predicate inStates = event.get("state").in(ev.getStates());
            predicate = cb.and(predicate, inStates);
        }
        if (ev.getCategories() != null) {
            Predicate category = event.get("category").in(ev.getCategories());
            predicate = cb.and(predicate, category);
        }
        if (ev.getRangeStart() != null) {
            Predicate start = cb.greaterThan(event.get("eventDate"), ev.getRangeStart());
            predicate = cb.and(predicate, start);
        }
        if (ev.getRangeEnd() != null) {
            Predicate end = cb.lessThan(event.get("eventDate"), ev.getRangeEnd());
            predicate = cb.and(predicate, end);
        }
        Predicate id = cb.greaterThanOrEqualTo(event.get("id"), ev.getFrom());
        predicate = cb.and(predicate, id);

        if (ev.getSort() != null && ev.getSort().equals(Sort.RATING)) {
            Join<Event, Like> likes = event.join("likes");
            query.select(event).where(predicate).groupBy(event).orderBy(cb.desc(cb.count(likes.get("id"))));
        } else {
            query.select(event).where(predicate);
        }
        return entityManager.createQuery(query).setMaxResults(ev.getSize()).getResultList();
    }
}

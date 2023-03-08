package ru.practicum.ewm.repository.event;

import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.util.EventState;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class EventRepositoryCBImpl implements EventRepositoryCB {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Event> publicSearch(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd, long from, int size) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);
        Predicate predicate = cb.conjunction();

        if (!text.isEmpty()) {
            Predicate annotation = cb.like(cb.lower(event.get("annotation")), "%" + text.toLowerCase() + "%");
            Predicate description = cb.like(cb.lower(event.get("description")), "%" + text.toLowerCase() + "%");
            Predicate hasText = cb.or(description, annotation);
            predicate = cb.and(predicate, hasText);
        }

        query.select(event).where(predicate);
        return entityManager.createQuery(query).setMaxResults(size).getResultList();
    }

    @Override
    public List<Event> adminSearch(List<Long> users, List<EventState> states, List<Long> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, long from, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);
        Predicate predicate = cb.conjunction();

        if (users != null) {
            Predicate inUsers = event.get("initiator").in(users);
            predicate = cb.and(predicate, inUsers);
        }
        if (states != null) {
            Predicate inStates = event.get("state").in(states);
            predicate = cb.and(predicate, inStates);
        }
        if (categories != null) {
            Predicate inCategories = event.get("category").in(categories);
            predicate = cb.and(predicate, inCategories);
        }
        if (rangeStart != null) {
            Predicate start = cb.greaterThan(event.get("eventDate"), rangeStart);
            predicate = cb.and(predicate, start);
        }
        if (rangeEnd != null) {
            Predicate end = cb.lessThan(event.get("eventDate"), rangeEnd);
            predicate = cb.and(predicate, end);
        }

        Predicate id = cb.greaterThanOrEqualTo(event.get("id"), from);
        predicate = cb.and(predicate, id);
        query.select(event).where(predicate);
        return entityManager.createQuery(query).setMaxResults(size).getResultList();
    }
}

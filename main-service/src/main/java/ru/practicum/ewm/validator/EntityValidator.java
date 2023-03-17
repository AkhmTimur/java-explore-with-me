package ru.practicum.ewm.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.compilation.Compilation;
import ru.practicum.ewm.model.eventLike.Like;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.category.CategoryRepository;
import ru.practicum.ewm.repository.compilation.CompilationRepository;
import ru.practicum.ewm.repository.event.EventRepository;
import ru.practicum.ewm.repository.like.LikeRepository;
import ru.practicum.ewm.repository.request.RequestRepository;
import ru.practicum.ewm.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class EntityValidator {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final LikeRepository likeRepository;

    public Category getCategoryIfExist(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(Category.class.getSimpleName() + " not found"));
    }

    public User getUserIfExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName() + " not found"));
    }

    public Event getEventIfExist(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Event.class.getSimpleName() + " not found"));
    }

    public Compilation getCompilationIfExist(Long comId) {
        return compilationRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException(Compilation.class.getSimpleName() + " not found"));
    }

    public ParticipationRequest getRequestIfExist(Long reqId) {
        return requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(ParticipationRequest.class.getSimpleName() + " not found"));
    }

    public Like getLikeIfExist(Event event, User user) {
        return likeRepository.findByEventIsAndUserIs(event, user);
    }
}

package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;

import static ru.practicum.ewm.mapper.CategoryMapper.categoryToDto;
import static ru.practicum.ewm.mapper.UserMapper.userToShortDto;

public class EventMapper {

    public static Event newEventDtoToEvent(NewEventDto e, Category category) {
        return Event.builder()
                .annotation(e.getAnnotation())
                .category(category)
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .location(e.getLocation())
                .paid(e.isPaid())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.isRequestModeration())
                .title(e.getTitle())
                .build();
    }

    public static EventFullDto eventToEventFullDto(Event e) {
        return EventFullDto.builder()
                .annotation(e.getAnnotation())
                .category(categoryToDto(e.getCategory()))
                .createdOn(e.getCreatedOn())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .id(e.getId())
                .initiator(userToShortDto(e.getInitiator()))
                .location(e.getLocation())
                .paid(e.getPaid())
                .participantLimit(e.getParticipantLimit())
                .publishedOn(e.getPublishedOn())
                .requestModeration(e.isRequestModeration())
                .state(e.getState())
                .title(e.getTitle())
                .views(0L)
                .build();
    }

    public static EventShortDto eventToEventShortDto(Event e) {
        return EventShortDto.builder()
                .annotation(e.getAnnotation())
                .category(categoryToDto(e.getCategory()))
                .eventDate(e.getEventDate())
                .id(e.getId())
                .initiator(userToShortDto(e.getInitiator()))
                .paid(e.getPaid())
                .title(e.getTitle())
                .build();
    }
}

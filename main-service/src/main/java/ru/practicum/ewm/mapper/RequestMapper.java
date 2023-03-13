package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.request.ParticipationRequest;

public class RequestMapper {
    public static ParticipationRequestDto requestToDto(ParticipationRequest pr) {
        return ParticipationRequestDto.builder()
                .created(pr.getCreated())
                .event(pr.getEvent().getId())
                .id(pr.getId())
                .requester(pr.getRequester().getId())
                .status(pr.getStatus())
                .build();
    }
}

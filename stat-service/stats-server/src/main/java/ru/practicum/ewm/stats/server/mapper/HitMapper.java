package ru.practicum.ewm.stats.server.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.server.model.Hit;

@Service
public class HitMapper {

    public Hit dtoToHit(HitDto hitDto) {
        return new Hit(null, hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
    }

    public HitDto hitToDto(Hit hit) {
        return new HitDto(hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());
    }

}

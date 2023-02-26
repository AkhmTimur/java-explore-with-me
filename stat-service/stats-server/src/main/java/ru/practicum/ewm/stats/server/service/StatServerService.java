package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.HitCountDto;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.server.mapper.HitMapper;
import ru.practicum.ewm.stats.server.model.Hit;
import ru.practicum.ewm.stats.server.repository.StatServerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServerService {
    private final HitMapper hitMapper;
    private final StatServerRepository repository;

    public HitDto createHit(HitDto hitDto) {
        Hit hit = hitMapper.dtoToHit(hitDto);
        return hitMapper.hitToDto(repository.save(hit));
    }

    public List<HitCountDto> getStats(LocalDateTime start, LocalDateTime end, boolean unique) {
        if (unique) {
            return repository.getStatsCreatedBetweenUnique(start, end);
        }
        return repository.getStatsCreatedBetween(start, end);
    }

    public List<HitCountDto> getStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            return repository.getStatsCreatedBetweenWithUrisUnique(start, end, uris);
        }
        return repository.getStatsCreatedBetweenWithUris(start, end, uris);
    }
}

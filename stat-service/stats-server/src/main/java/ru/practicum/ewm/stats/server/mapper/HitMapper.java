package ru.practicum.ewm.stats.server.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.server.model.Hit;
import ru.practicum.ewm.stats.server.repository.AppRepository;

import java.util.Optional;

@Service
public class HitMapper {
    private final AppRepository appRepository;

    public HitMapper(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public Hit dtoToHit(HitDto hitDto) {
        Optional<App> app = appRepository.findByAppName(hitDto.getApp());
        if(app.isPresent()) {
            return new Hit(null, app.get(), hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
        } else {
            App newApp = appRepository.save(new App(hitDto.getApp()));
            return new Hit(null, newApp, hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
        }
    }

    public HitDto hitToDto(Hit hit) {
        return new HitDto(hit.getApp().getAppName(), hit.getUri(), hit.getIp(), hit.getTimestamp());
    }

}

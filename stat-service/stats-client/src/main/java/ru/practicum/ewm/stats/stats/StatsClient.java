package ru.practicum.ewm.stats.stats;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stats.client.BaseClient;
import ru.practicum.ewm.stats.dto.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StatsClient extends BaseClient {

    public StatsClient(String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> createHit(HitDto hitInDto) {
        return post("/hit", hitInDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, Optional<List<String>> uri, boolean unique) {
        String path;
        Map<String, Object> parameters;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (uri.isPresent()) {
            path = "/stats?start={start}&end={end}&uri={uri}&unique={unique}";
            parameters = Map.of(
                    "start", start.format(formatter),
                    "end", end.format(formatter),
                    "uri", uri.get(),
                    "unique", unique
            );
        } else {
            path = "/stats?start={start}&end={end}&unique={unique}";
            parameters = Map.of("start", start.format(formatter), "end", end.format(formatter),
                    "unique", unique
            );
        }
        return get(path, parameters);
    }
}

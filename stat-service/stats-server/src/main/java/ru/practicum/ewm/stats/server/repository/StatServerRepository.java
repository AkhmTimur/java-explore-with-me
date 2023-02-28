package ru.practicum.ewm.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.dto.HitCountDto;
import ru.practicum.ewm.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatServerRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.ewm.stats.dto.HitCountDto(h.app.appName, h.uri, COUNT(h.id))" +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC ")
    List<HitCountDto> getStatsCreatedBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.stats.dto.HitCountDto(h.app.appName, h.uri, COUNT(h.id))" +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 AND h.uri IN (?3)" +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC ")
    List<HitCountDto> getStatsCreatedBetweenWithUris(LocalDateTime start, LocalDateTime end, Collection<String> uris);

    @Query("SELECT new ru.practicum.ewm.stats.dto.HitCountDto(h.app.appName, h.uri, COUNT(DISTINCT h.id))" +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.id) DESC ")
    List<HitCountDto> getStatsCreatedBetweenUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.stats.dto.HitCountDto(h.app.appName, h.uri, COUNT(DISTINCT h.id))" +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 AND h.uri IN (?3) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.id) DESC ")
    List<HitCountDto> getStatsCreatedBetweenWithUrisUnique(LocalDateTime start, LocalDateTime end, Collection<String> uris);
}

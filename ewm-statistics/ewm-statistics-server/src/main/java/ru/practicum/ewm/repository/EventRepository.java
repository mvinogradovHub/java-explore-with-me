package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EndpointHit, Long> {
    @Query(" SELECT new ru.practicum.ewm.model.ViewStats (he.app, he.uri, COUNT(he.ip)) " +
            "FROM EndpointHit he " +
            "WHERE he.timestamp BETWEEN ?1 AND ?2 " +
            "AND (he.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY he.app, he.uri " +
            "ORDER BY COUNT(he.ip) DESC "
    )
    List<ViewStats> getEvents(LocalDateTime start, LocalDateTime end, List<String> uris);


    @Query(" SELECT new ru.practicum.ewm.model.ViewStats(he.app, he.uri, COUNT(DISTINCT he.ip)) " +
            "FROM EndpointHit he " +
            "WHERE he.timestamp BETWEEN ?1 AND ?2 " +
            "AND (he.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY he.app, he.uri " +
            "ORDER BY COUNT(he.ip) DESC "
    )
    List<ViewStats> getUniqueEvents(LocalDateTime start, LocalDateTime end, List<String> uris);
}

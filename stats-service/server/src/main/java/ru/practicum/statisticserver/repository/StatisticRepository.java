package ru.practicum.statisticserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.statdto.ViewStats;
import ru.practicum.statisticserver.model.EndpointHit;
import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long> {
@Query("SELECT h.app AS app, h.uri AS uri, COUNT(DISTINCT h.ip) AS hits " +
        "FROM EndpointHit h " +
        "GROUP BY h.app, h.uri " +
        "ORDER BY hits DESC")

List<ViewStats> findUniqueStatData(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("uris") List<String> uris);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(h.ip) AS hits " +
            "FROM EndpointHit h " +
            "WHERE h.created BETWEEN :start AND :end " +
            "AND ((:uris) IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<ViewStats> findUnUniqueStatData(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end,
                                         @Param("uris") List<String> uris);
}
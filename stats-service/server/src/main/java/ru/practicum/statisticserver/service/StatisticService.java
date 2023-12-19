package ru.practicum.statisticserver.service;

import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;
import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    EndpointHitDto addStatisticData(EndpointHitDto endpointHit);

    List<ViewStatsDto> getStatisticData(LocalDateTime start, LocalDateTime end, List<String> uris, boolean isUnique);
}
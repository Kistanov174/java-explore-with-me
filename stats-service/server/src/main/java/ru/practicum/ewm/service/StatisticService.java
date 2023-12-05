package ru.practicum.ewm.service;

import ru.practicum.ewm.statdto.EndpointHitDto;
import ru.practicum.ewm.statdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    EndpointHitDto addStatisticData(EndpointHitDto endpointHit);

    List<ViewStatsDto> getStatisticData(LocalDateTime start, LocalDateTime end, String[] uris, boolean isUnique);
}

package ru.practicum.statisticserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStats;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statisticserver.model.EndpointHit;
import ru.practicum.statisticserver.repository.StatisticRepository;
import ru.practicum.statisticserver.service.StatisticService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository statisticRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public EndpointHitDto addStatisticData(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = modelMapper.map(endpointHitDto, EndpointHit.class);
        EndpointHitDto savingEndpointHit = modelMapper.map(statisticRepository.save(endpointHit), EndpointHitDto.class);
        log.info("Created new line in statistic data = {}", savingEndpointHit.toString());
        return savingEndpointHit;
    }

    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStatisticData(LocalDateTime start, LocalDateTime end,
                                            List<String> uris, boolean isUnique) {
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date range");
        }

        List<ViewStats> statisticData;
        if (isUnique) {
            statisticData = statisticRepository.findUniqueStatData(start, end, uris);
        } else {
            statisticData = statisticRepository.findUnUniqueStatData(start, end, uris);
        }
        log.info("Received statistic data by uri = {}", uris.toString());
        return statisticData.stream()
                .map(line -> ViewStatsDto.builder()
                        .app(line.getApp())
                        .uri(line.getUri())
                        .hits(line.getHits())
                        .build())
                .collect(Collectors.toList());
    }
}
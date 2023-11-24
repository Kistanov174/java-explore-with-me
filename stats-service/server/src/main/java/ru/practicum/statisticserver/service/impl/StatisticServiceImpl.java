package ru.practicum.statisticserver.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStats;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statisticserver.model.EndpointHit;
import ru.practicum.statisticserver.repository.StatisticRepository;
import ru.practicum.statisticserver.service.StatisticService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private StatisticRepository statisticRepository;
    private ModelMapper modelMapper;

    public EndpointHitDto addStatisticData(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = modelMapper.map(endpointHitDto, EndpointHit.class);
        EndpointHitDto savingEndpointHit = modelMapper.map(statisticRepository.save(endpointHit), EndpointHitDto.class);
        log.info("Created new line in statistic data = {}", savingEndpointHit.toString());
        return savingEndpointHit;
    }

    public List<ViewStatsDto> getStatisticData(LocalDateTime start, LocalDateTime end,
                                            String[] uris, boolean isUnique) {
        List<ViewStats> statisticData;
        if (uris == null || uris.length == 0) {
            if (isUnique) {
                statisticData = statisticRepository.countByTimestampUniqueIp(start, end);
            } else {
                statisticData = statisticRepository.countByTimestamp(start, end);
            }
        } else {
            if (isUnique) {
                statisticData = statisticRepository.findStatisticWithUnique(start, end, uris);
            } else {
                statisticData = statisticRepository.findStatisticNotUnique(start, end, uris);
            }
        }
        log.info("Received statistic data by uri = {}",  Arrays.toString(uris));
        return statisticData.stream()
                .map(data -> modelMapper.map(data, ViewStatsDto.class))
                .collect(Collectors.toList());
    }
}
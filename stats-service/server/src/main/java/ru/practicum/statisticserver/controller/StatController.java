package ru.practicum.statisticserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statisticserver.service.StatisticService;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {
    private final StatisticService statisticService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> saveStatistic(@RequestBody @Valid EndpointHitDto endpointHit) {
        log.info("Created request to save information about calling of the endpoint " + endpointHit.getUri());
        endpointHit.setCreated(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(statisticService.addStatisticData(endpointHit));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStatistic(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                   LocalDateTime start,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                   LocalDateTime end,
                                      @RequestParam(required = false) String[] uris,
                                      @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Created request to get statistic data");
        if (end.isBefore(start) || end.isEqual(start)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(statisticService.getStatisticData(start, end, uris, unique), HttpStatus.OK);
    }
}
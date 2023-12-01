package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.SearchParameters;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.SortBy;
import ru.practicum.mainservice.event.service.EventService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import static ru.practicum.mainservice.config.Constant.DATE_FORMAT;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {
    private final EventMapper eventMapper;
    private final EventService eventService;

    @GetMapping
    public Collection<EventShortDto> search(@RequestParam(value = "text", required = false) String text,
                                            @RequestParam(value = "categories", required = false) List<Long> categories,
                                            @RequestParam(value = "paid", required = false) Boolean paid,
                                            @RequestParam(value = "rangeStart", required = false)
                                                @DateTimeFormat(pattern = DATE_FORMAT)
                                            LocalDateTime rangeStart,
                                            @RequestParam(value = "rangeEnd", required = false)
                                                @DateTimeFormat(pattern = DATE_FORMAT)
                                            LocalDateTime rangeEnd,
                                            @RequestParam(value = "onlyAvailable", defaultValue = "false",
                                                    required = false)
                                            boolean onlyAvailable,
                                            @RequestParam (value = "from", defaultValue = "0", required = false)
                                                @Min(0)  int from,
                                            @RequestParam (value = "size", defaultValue = "10", required = false)
                                                @Min(1) int size,
                                            @RequestParam (value = "sort", defaultValue = "EVENT_DATE",
                                                    required = false) String sortBy,
                                            HttpServletRequest request) {
        SearchParameters parameters = SearchParameters.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .page(from / size)
                .size(size)
                .sort(SortBy.valueOf(sortBy))
                .request(request)
                .build();
        log.info("GET request: запрос событий с парметрами {}", parameters);
        return eventService.search(parameters).stream()
                .map(eventMapper::convertToShortDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable("id") long eventId, HttpServletRequest request) {
        log.info("GET request: запрос события c id={}", eventId);
        return eventMapper.convertToFullDto(eventService.getById(eventId, request));
    }
}
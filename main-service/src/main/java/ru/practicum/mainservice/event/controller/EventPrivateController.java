package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.mainservice.config.Create;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventRequest;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.service.EventService;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventMapper eventMapper;
    private final EventService eventService;

    @PostMapping
    public EventFullDto create(@Validated({Create.class})
                               @RequestBody NewEventDto event,
                               @PathVariable Long userId) {
        log.info("POST request: создание события {}", event);
        return eventMapper.convertToFullDto(eventService.create(event, userId));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByOwner(@PathVariable Long userId,
                                   @PathVariable Long eventId) {
        log.info("GET request: запрос события {} пользователем {}", eventId, userId);
        return eventMapper.convertToFullDto(eventService.getByOwner(userId, eventId));
    }

    @GetMapping
    public Collection<EventShortDto> getAllByOwner(@RequestParam (defaultValue = "0", required = false) @Min(0)  int from,
                                                   @RequestParam (defaultValue = "10", required = false) @Min(1) int size,
                                                   @PathVariable Long userId) {
        log.info("GET request: запрос событий пользователя {}", userId);
        return eventService.getAllByOwner(userId, PageRequest.of(from / size, size)).stream()
                .map(eventMapper::convertToShortDto)
                .collect(Collectors.toList());
    }

    @PatchMapping
    public EventFullDto update(@RequestBody UpdateEventRequest eventRequest,
                               @PathVariable Long userId) {
        log.info("PATCH request: изменения события {}", eventRequest);
        return eventMapper.convertToFullDto(eventService.update(userId, eventRequest));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancelEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId) {
        log.info("PATCH request: отмена события {} пользователя {}",eventId, userId);
        return eventMapper.convertToFullDto(eventService.cancel(userId, eventId));
    }
}
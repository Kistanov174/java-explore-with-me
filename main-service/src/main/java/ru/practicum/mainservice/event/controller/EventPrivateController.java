package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.EventUserDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.RequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    ResponseEntity<EventFullDto> addEvent(@Positive @PathVariable("userId") Long userId,
                                          @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Получен запрос POST на добавление категории {}", newEventDto.toString());
        return new ResponseEntity<>(eventService.addEvent(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<List<EventShortDto>> findAllUserEvents(@Positive @PathVariable("userId") Long userId,
                                                          @PositiveOrZero
                                                          @RequestParam(defaultValue = "0") int from,
                                                          @Positive
                                                          @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Получен GET запрос на просмотр событий, добавленных пользователем {}", userId);
        return ResponseEntity.ok(eventService.findAllUserEvents(userId, PageRequest.of(from / size, size)));
    }

    @GetMapping("/{eventId}")
    ResponseEntity<EventFullDto> findUserEvent(@Positive @PathVariable("userId") Long userId,
                                         @Positive @PathVariable("eventId") Long eventId) {
        log.info("Получен GET запрос на просмотр события, добавленного пользователем {}", userId);
        return ResponseEntity.ok(eventService.findUserEvent(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    ResponseEntity<EventFullDto> updateEvent(@Positive @PathVariable("userId") Long userId,
                                       @Positive @PathVariable("eventId") Long eventId,
                                       @Valid @RequestBody EventUserDto updateEvent) {
        log.info("Получен PATCH запрос на обновление события пользователем {}", userId);
        return ResponseEntity.ok(eventService.updateEventByUser(userId, eventId, updateEvent));
    }

    @GetMapping("/{eventId}/requests")
    ResponseEntity<List<RequestDto>> getInfoAboutRequests(@Positive @PathVariable("userId") Long userId,
                                                    @Positive @PathVariable("eventId") Long eventId) {
        log.info("Получен GET запрос от инициатора {} события {} на просмотр запросов на участие в событии",
                userId, eventId);
        return ResponseEntity.ok(eventService.getInfoAboutRequests(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(@Positive @PathVariable("userId") Long userId,
                                                                       @Positive @PathVariable("eventId") Long eventId,
                                                                       @RequestBody
                                                                       EventRequestStatusUpdateRequest updateRequest) {
        log.info("Получен PATCH запрос на обновление статусов запросов на участие в событии {}", eventId);
        return ResponseEntity.ok(eventService.updateRequestStatus(userId, eventId, updateRequest));
    }
}
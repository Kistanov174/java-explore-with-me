package ru.practicum.ewm.server.service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.ewm.server.service.event.dto.EventUserDto;
import ru.practicum.ewm.server.service.event.dto.NewEventDto;
import ru.practicum.ewm.server.service.event.service.EventService;
import ru.practicum.ewm.server.service.request.dto.EventRequestStatusUpdateRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    ResponseEntity<Object> addEvent(@Positive @PathVariable("userId") Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Получен запрос POST на добавление категории {}", newEventDto.toString());
        return new ResponseEntity<>(eventService.addEvent(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<Object> findAllUserEvents(@Positive @PathVariable("userId") Long userId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Получен GET запрос на просмотр событий, добавленных пользователем {}", userId);
        return new ResponseEntity<>(eventService.findAllUserEvents(userId, PageRequest.of(from / size, size)),
                HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    ResponseEntity<Object> findUserEvent(@Positive @PathVariable("userId") Long userId,
                                         @Positive @PathVariable("eventId") Long eventId) {
        log.info("Получен GET запрос на просмотр события, добавленного пользователем {}", userId);
        return new ResponseEntity<>(eventService.findUserEvent(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    ResponseEntity<Object> updateEvent(@Positive @PathVariable("userId") Long userId,
                                       @Positive @PathVariable("eventId") Long eventId,
                                       @Valid @RequestBody EventUserDto updateEvent) {
        log.info("Получен PATCH запрос на обновление события пользователем {}", userId);
        return new ResponseEntity<>(eventService.updateEventByUser(userId, eventId, updateEvent), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    ResponseEntity<Object> getInfoAboutRequests(@Positive @PathVariable("userId") Long userId,
                                                @Positive @PathVariable("eventId") Long eventId) {
        log.info("Получен GET запрос от инициатора {} события {} на просмотр запросов на участие в событии",
                userId, eventId);
        return new ResponseEntity<>(eventService.getInfoAboutRequests(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    ResponseEntity<Object> updateRequestStatus(@Positive @PathVariable("userId") Long userId,
                                               @Positive @PathVariable("eventId") Long eventId,
                                               @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Получен PATCH запрос на обновление статусов запросов на участие в событии {}", eventId);
        return new ResponseEntity<>(eventService.updateRequestStatus(userId, eventId, updateRequest), HttpStatus.OK);
    }
}
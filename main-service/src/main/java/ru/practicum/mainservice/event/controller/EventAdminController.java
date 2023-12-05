package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.EventAdminDto;
import ru.practicum.mainservice.event.model.State;
import ru.practicum.mainservice.event.service.EventService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventAdminController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    ResponseEntity<Object> updateEvent(@Positive @PathVariable("eventId") Long eventId,
                                       @Valid @RequestBody EventAdminDto updateEvent) {
        log.info("Получен PATCH запрос на обновление события {} администратором", eventId);
        return new ResponseEntity<>(eventService.updateEventByAdmin(eventId, updateEvent), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> findAllEvents(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<State> states,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET запрос на просмотр событий по фильтрам");
        return new ResponseEntity<>(eventService.getEventsForAdmin(users, states, categories, rangeStart,
                rangeEnd, from, size), HttpStatus.OK);
    }
}
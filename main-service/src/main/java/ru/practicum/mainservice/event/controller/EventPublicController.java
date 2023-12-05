package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.exception.ValidationException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Object> findAnyEvents(@RequestParam(required = false) String text,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) Boolean paid,
                                                @RequestParam(required = false)
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                LocalDateTime rangeStart,
                                                @RequestParam(required = false)
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                LocalDateTime rangeEnd,
                                                @RequestParam(required = false, defaultValue = "false")
                                                    Boolean onlyAvailable,
                                                @RequestParam(required = false, defaultValue = "EVENT_DATE")
                                                    String sort,
                                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                                @Positive @RequestParam(required = false, defaultValue = "10") int size,
                                                HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && !rangeEnd.isAfter(rangeStart)) {
            throw new ValidationException("rangeStart не может быть после rangeEnd");
        }
        log.info("Получен GET запрос на просмотр событий по фильтрам");
        return new ResponseEntity<>(eventService.getEventsForPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFullEventInfo(@Positive @PathVariable Long id, HttpServletRequest request) {
        log.info("Получен GET запрос на просмотр события по id {}", id);
        return new ResponseEntity<>(eventService.findEventById(id, request), HttpStatus.OK);
    }
}
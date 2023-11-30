package ru.practicum.mainservice.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.mainservice.exception.ValidationException;
import ru.practicum.mainservice.request.service.RequestService;

import javax.validation.constraints.Positive;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService service;

    @PostMapping
    ResponseEntity<Object> addRequest(@Positive @PathVariable("userId") Long userId,
                                      @RequestParam(required = false) Long eventId) {
        if (eventId == null) {
            throw new ValidationException("Не передан обязательный query parameter");
        }
        log.info("Получен запрос POST на создание запроса на участие в событии {}", eventId);
        return new ResponseEntity<>(service.addRequest(userId, eventId), HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<Object> findAllRequests(@Positive @PathVariable("userId") Long userId) {
        log.info("Получен GET запрос на просмотр запросов пользователя {} на участие в событиях", userId);
        return new ResponseEntity<>(service.findAllByRequesterId(userId), HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/cancel")
    ResponseEntity<Object> cancelRequest(@Positive @PathVariable("userId") Long userId,
                                         @Positive @PathVariable("requestId") Long requestId) {
        log.info("Получен PATCH запрос на отмену запроса {} на участие в событии пользователем {}", requestId, userId);
        return new ResponseEntity<>(service.changeStatusToCancelled(userId, requestId), HttpStatus.OK);
    }
}
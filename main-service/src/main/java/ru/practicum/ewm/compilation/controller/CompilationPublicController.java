package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.compilation.service.CompilationService;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    ResponseEntity<Object> findById(@Positive @PathVariable("compId") Long compId) {
        log.info("Получен запрос GET для подборки событий по id {}", compId);
        return new ResponseEntity<>(compilationService.findById(compId), HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<Object> findAll(@RequestParam(required = false) Boolean pinned,
                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                   @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Получен GET запрос на просмотр подборок событий");
        return new ResponseEntity<>(compilationService.findAll(pinned, PageRequest.of(from / size, size)),
                HttpStatus.OK);
    }
}
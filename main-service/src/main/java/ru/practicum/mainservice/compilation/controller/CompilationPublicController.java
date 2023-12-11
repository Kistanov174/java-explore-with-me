package ru.practicum.mainservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.service.CompilationService;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    ResponseEntity<CompilationDto> findById(@Positive @PathVariable("compId") Long compId) {
        log.info("Получен запрос GET для подборки событий по id {}", compId);
        return ResponseEntity.ok(compilationService.findById(compId));
    }

    @GetMapping
    ResponseEntity<List<CompilationDto>> findAll(@RequestParam(required = false) Boolean pinned,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Получен GET запрос на просмотр подборок событий");
        return ResponseEntity.ok(compilationService.findAll(pinned, PageRequest.of(from / size, size)));
    }
}
package ru.practicum.mainservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.compilation.service.CompilationService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    ResponseEntity<Object> addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Получен запрос POST на создание подборки событий {}", newCompilationDto.toString());
        return new ResponseEntity<>(compilationService.addCompilation(newCompilationDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    ResponseEntity<Object> updateCompilation(@Positive @PathVariable("compId") Long compId,
                                             @Valid @RequestBody UpdateCompilationRequest updCompReq) {
        log.info("Получен запрос PATCH на обновление подборки событий {}", updCompReq.toString());
        return new ResponseEntity<>(compilationService.updateCompilation(compId, updCompReq), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    ResponseEntity<Object> deleteCompilation(@Positive @PathVariable("compId") Long compId) {
        log.info("Получен запрос DELETE для подборки событий по id {}", compId);
        return new ResponseEntity<>(compilationService.deleteCompilation(compId), HttpStatus.NO_CONTENT);
    }
}
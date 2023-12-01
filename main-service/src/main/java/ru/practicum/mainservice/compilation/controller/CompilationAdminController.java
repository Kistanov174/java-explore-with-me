package ru.practicum.mainservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.practicum.mainservice.compilation.service.CompilationService;
import ru.practicum.mainservice.config.Create;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {
    private final CompilationMapper compilationMapper;
    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto create(@Validated({Create.class})
                                 @RequestBody NewCompilationDto compilationDto) {
        log.info("POST request: создание подборки событий {}", compilationDto);
        return compilationMapper.convertToDto(compilationService.create(compilationDto));
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEvent(@PathVariable Long compId,
                         @PathVariable Long eventId) {
        log.info("PATCH request: добавление в подборку {} события {}", compId, eventId);
        compilationService.addEvent(compId, eventId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilation(@PathVariable Long compId) {
        log.info("PATCH request: закрепление подборки id={}", compId);
        compilationService.pinCompilation(compId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpinCompilation(@PathVariable Long compId) {
        log.info("DELETE request: открепление подборки id={}", compId);
        compilationService.unpinCompilation(compId);
    }

    @DeleteMapping("/{compId}")
    public void delete(@PathVariable Long compId) {
        log.info("DELETE request: удалаение подборки id={}", compId);
        compilationService.delete(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable Long compId,
                            @PathVariable Long eventId) {
        log.info("DELETE request: удалаение события {} из подборки id={}",eventId, compId);
        compilationService.deleteEvent(compId, eventId);
    }
}
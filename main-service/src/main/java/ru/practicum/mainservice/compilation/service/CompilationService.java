package ru.practicum.mainservice.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.model.Compilation;
import java.util.Collection;

public interface CompilationService {
    Collection<Compilation> getAll(Boolean pinned, Pageable pageable);

    Compilation getById(Long compId);

    Compilation create(NewCompilationDto compilationDto);

    void addEvent(Long compId, Long eventId);

    void pinCompilation(Long compId);

    void unpinCompilation(Long compId);

    void delete(Long compId);

    void deleteEvent(Long compId, Long eventId);
}
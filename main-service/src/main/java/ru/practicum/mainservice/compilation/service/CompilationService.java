package ru.practicum.mainservice.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.dto.UpdateCompilationRequest;
import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    boolean deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest upCompReq);

    CompilationDto findById(Long compId);

    List<CompilationDto> findAll(Boolean pinned, PageRequest pageRequest);
}
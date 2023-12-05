package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    boolean deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest upCompReq);

    CompilationDto findById(Long compId);

    List<CompilationDto> findAll(Boolean pinned, PageRequest pageRequest);
}
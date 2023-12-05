package ru.practicum.ewm.server.service.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.server.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.server.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.server.service.compilation.dto.UpdateCompilationRequest;
import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    boolean deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest upCompReq);

    CompilationDto findById(Long compId);

    List<CompilationDto> findAll(Boolean pinned, PageRequest pageRequest);
}
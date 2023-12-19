package ru.practicum.mainservice.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.compilation.mapper.CompilationConvector;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.compilation.service.CompilationService;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.DataNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationConvector mapper;

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        if (checkingUniqueTitle(newCompilationDto.getTitle())) {
            throw new ConflictException("could not execute statement;" +
                    " SQL [n/a]; constraint [uq_compilation_title];" +
                    " nested exception is org.hibernate.exception.ConstraintViolationException:" +
                    " could not execute statement");
        }
        Compilation compilation = mapper.toEntityFromNew(newCompilationDto);
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
            compilation.setEvents(new HashSet<>(events));
        }
        CompilationDto compilationDto = mapper.toDto(compilationRepository.save(compilation));
        log.info("Добавлена новая подборка событий {}", compilationDto);
        return compilationDto;
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest upCompReq) {
        Compilation compilation = getCompilation(compId);

        Provider<Compilation> compProvider = p -> compilation;
        mapper.getPropertyMapper().setProvider(compProvider);

        Compilation actualComp = mapper.toEntityFromUpdate(upCompReq);
        if (upCompReq.getEvents() != null && !upCompReq.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(upCompReq.getEvents());
            compilation.setEvents(new HashSet<>(events));
            actualComp.getEvents().addAll(events);
        }

        CompilationDto compilationDto = mapper.toDto(compilationRepository.save(actualComp));
        log.info("Подборка событий обновлена {}", compilationDto);
        return compilationDto;
    }

    @Transactional
    @Override
    public boolean deleteCompilation(Long compId) {
        compilationRepository.delete(getCompilation(compId));
        log.info("Подборка событий с id {} удалена", compId);
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> findAll(Boolean pinned, PageRequest pageRequest) {
        Page<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest);
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        }

        List<CompilationDto> compilationsDto = compilations.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        log.info("Запрошен список категорий {}", compilationsDto);
        return compilationsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto findById(Long compId) {
        CompilationDto compilationDto = mapper.toDto(getCompilation(compId));
        log.info("Запрошена подборка событий по id {}", compilationDto);
        return compilationDto;
    }

    private boolean checkingUniqueTitle(String title) {
        return compilationRepository.existsByTitleIgnoreCase(title);
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found"));
    }
}
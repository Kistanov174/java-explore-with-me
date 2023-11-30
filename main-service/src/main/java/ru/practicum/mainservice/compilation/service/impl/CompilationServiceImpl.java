package ru.practicum.mainservice.compilation.service.impl;

import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.compilation.mapper.CompilationConvector;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.compilation.service.CompilationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.DataNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationConvector mapper;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        if (checkingUniqTitle(newCompilationDto.getTitle())) {
            throw new ConflictException("could not execute statement;" +
                    " SQL [n/a]; constraint [uq_compilation_title];" +
                    " nested exception is org.hibernate.exception.ConstraintViolationException:" +
                    " could not execute statement");
        }
        Compilation compilation = mapper.toEntityFromNew(newCompilationDto);
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            List<Event> events = newCompilationDto.getEvents()
                    .stream()
                    .map(this::getEvent)
                    .collect(Collectors.toList());
            compilation.setEvents(events);
        }
        CompilationDto compilationDto = mapper.toDto(compilationRepository.save(compilation));
        log.info("Добавлена новая подборка событий {}", compilationDto);
        return compilationDto;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest upCompReq) {
        Compilation compilation = getCompilation(compId);


        Provider<Compilation> compProvider = p -> compilation;
        mapper.getPropertyMapper().setProvider(compProvider);

        Compilation actualComp = mapper.toEntityFromUpdate(upCompReq);
        if (upCompReq.getEvents() != null && !upCompReq.getEvents().isEmpty()) {
            List<Event> events = upCompReq.getEvents()
                    .stream()
                    .map(this::getEvent)
                    .collect(Collectors.toList());
            actualComp.getEvents().addAll(events);
        }

        CompilationDto compilationDto = mapper.toDto(compilationRepository.save(actualComp));
        log.info("Подборка событий обновлена {}", compilationDto);
        return compilationDto;
    }

    @Override
    public boolean deleteCompilation(Long compId) {
        compilationRepository.delete(getCompilation(compId));
        log.info("Подборка событий с id {} удалена", compId);
        return true;
    }

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

    @Override
    public CompilationDto findById(Long compId) {
        CompilationDto compilationDto = mapper.toDto(getCompilation(compId));
        log.info("Запрошена подборка событий по id {}", compilationDto);
        return compilationDto;
    }

    private boolean checkingUniqTitle(String title) {
        return compilationRepository.findCompilationByTitleIgnoreCase(title) != null;
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found"));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Event with id=" + id + " was not found"));
    }
}
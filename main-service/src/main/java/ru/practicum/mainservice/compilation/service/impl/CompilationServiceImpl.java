package ru.practicum.mainservice.compilation.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.compilation.service.CompilationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.NotFoundException;
import java.util.Collection;
import static ru.practicum.mainservice.config.Constant.COMPILATION;
import static ru.practicum.mainservice.config.Constant.EVENT;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<Compilation> getAll(Boolean pinned, Pageable pageable) {
        return compilationRepository.findAllByPinned(pinned, pageable).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Compilation getById(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION, compId));
    }

    @Override
    @Transactional
    public Compilation create(NewCompilationDto compilationDto) {
        return compilationRepository.save(compilationMapper.convert(compilationDto));
    }

    @Override
    @Transactional
    public void addEvent(Long compId, Long eventId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION, compId));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT,eventId));
        compilation.getEvents().add(event);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void pinCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION, compId));
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void unpinCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION, compId));
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public void deleteEvent(Long compId, Long eventId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(COMPILATION, compId));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT,eventId));
        compilation.getEvents().remove(event);
        compilationRepository.save(compilation);
    }
}
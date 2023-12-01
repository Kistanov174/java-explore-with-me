package ru.practicum.mainservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.NotFoundException;

import static ru.practicum.mainservice.config.Constant.EVENT;

@Component
@RequiredArgsConstructor
public class EntityReferenceMapper {
    private final EventRepository eventRepository;

    public Event getById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT, eventId));
    }
}
package ru.practicum.mainservice.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.mainservice.event.dto.SearchParameters;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventRequest;
import ru.practicum.mainservice.event.dto.AdminUpdateEventDto;
import ru.practicum.mainservice.event.model.Event;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public interface EventService {
    Collection<Event> search(SearchParameters searchParameters);

    Collection<Event> getAllAdmin(SearchParameters searchParameters);

    Collection<Event> getAllByOwner(Long userId, Pageable pageable);

    Event getById(Long eventId, HttpServletRequest request);

    Event getByOwner(Long userId, Long eventId);

    Event create(NewEventDto newEventDto, Long userId);

    Event update(long userId, UpdateEventRequest eventRequest);

    Event updateAdmin(AdminUpdateEventDto eventDto, Long eventId);

    Event cancel(Long userId, Long eventId);

    Event publish(Long eventId);

    Event reject(Long eventId);
}
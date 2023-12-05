package ru.practicum.ewm.server.service.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.server.service.event.dto.*;
import ru.practicum.ewm.server.service.event.model.State;
import ru.practicum.ewm.server.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.server.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.server.service.request.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> findAllUserEvents(Long userId, PageRequest pageRequest);

    EventFullDto findUserEvent(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, EventUserDto updateEvent);

    List<RequestDto> getInfoAboutRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);

    EventFullDto updateEventByAdmin(Long eventId, EventAdminDto updEventReq);

    List<EventFullDto> getEventsForAdmin(List<Long> users, List<State> states, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<EventPublicDto> getEventsForPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from,
                                            int size, HttpServletRequest request);

    EventFullDto findEventById(Long id, HttpServletRequest request);
}

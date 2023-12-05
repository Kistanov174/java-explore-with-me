package ru.practicum.ewm.server.service.request.service;

import ru.practicum.ewm.server.service.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(Long userId, Long eventId);

    List<RequestDto> findAllByRequesterId(Long userId);

    RequestDto changeStatusToCancelled(Long userId, Long requestId);
}
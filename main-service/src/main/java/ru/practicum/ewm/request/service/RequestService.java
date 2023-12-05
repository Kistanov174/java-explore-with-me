package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(Long userId, Long eventId);

    List<RequestDto> findAllByRequesterId(Long userId);

    RequestDto changeStatusToCancelled(Long userId, Long requestId);
}
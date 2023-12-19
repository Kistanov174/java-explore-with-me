package ru.practicum.mainservice.request.service;


import ru.practicum.mainservice.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(Long userId, Long eventId);

    List<RequestDto> findAllByRequesterId(Long userId);

    RequestDto changeStatusToCancelled(Long userId, Long requestId);
}
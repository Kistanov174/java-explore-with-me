package ru.practicum.mainservice.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.mainservice.request.dto.RequestDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.service.RequestService;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class RequestController {
    private final RequestMapper requestMapper;
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    public RequestDto create(@PathVariable Long userId,
                             @RequestParam @Positive Long eventId) {
        return requestMapper.convertToDto(requestService.create(userId, eventId));
    }

    @GetMapping("/users/{userId}/requests")
    public Collection<RequestDto> getAll(@PathVariable Long userId) {
        return requestService.getAll(userId).stream()
                .map(requestMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDto cancel(@PathVariable Long userId,
                             @PathVariable Long requestId) {
        return requestMapper.convertToDto(requestService.cancel(userId, requestId));
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public Collection<RequestDto> getRequestsByEventOwner(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        log.info("GET request: запрос заявок на участии в событии {} пользователя {}",eventId, userId);
        return requestService.getRequestsByEventOwner(userId, eventId).stream()
                .map(requestMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmByEventOwner(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @PathVariable("reqId") Long requestId) {
        return requestMapper.convertToDto(requestService.confirmByEventOwner(userId, eventId, requestId));
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/reject")
    public RequestDto cancelByEventOwner(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @PathVariable("reqId") Long requestId) {
        return requestMapper.convertToDto(requestService.rejectByEventOwner(userId, eventId, requestId));
    }
}
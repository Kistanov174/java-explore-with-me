package ru.practicum.ewm.request.service.impl;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.DataNotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper modelMapper;

    @Transactional
    @Override
    public RequestDto addRequest(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        if (requestRepository.findByRequester_IdAndEvent_Id(userId, eventId) != null ||
                userId.equals(event.getInitiator().getId()) ||
                !event.getState().equals(State.PUBLISHED) ||
                (event.getParticipantLimit() != 0 && event.getParticipantLimit() - event.getConfirmedRequests() == 0)
        ) {
            throw new ConflictException("Запрос не соответствует требованиям. Возможные проблемы: попытка добавить" +
                    " повторный запрос; инициатор события пытается добавить запрос на участие в своём событии;" +
                    " событие не опубликованно; у события достигнут лимит запросов на участие;");
        }

        Request newRequest = new Request(null, event, LocalDateTime.now(), user, null);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }
        Request request = requestRepository.save(newRequest);
        RequestDto requestDto = new RequestDto(
                request.getId(),
                request.getEvent().getId(),
                request.getCreated(),
                request.getRequester().getId(),
                request.getStatus());
        log.info("Добавлен новый запрос на участие в событии {}", requestDto);
        return requestDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> findAllByRequesterId(Long userId) {
        getUser(userId);
        List<RequestDto> requests = requestRepository.findAllByRequester_Id(userId).stream()
                .map(modelMapper::toDto)
                .collect(Collectors.toList());
        log.info("Все запросы пользователя {} на участие в событии {}", userId, requests);
        return requests;
    }

    @Transactional
    @Override
    public RequestDto changeStatusToCancelled(Long userId, Long requestId) {
        Request request = getRequest(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("Пользователь " + userId + " не является инициатором запроса на участие в событии");
        }
        request.setStatus(RequestStatus.CANCELED);
        RequestDto requestDto = modelMapper.toDto(requestRepository.save(request));
        log.info("Пользователь {} отменил свой запрос на участие {}", userId, requestId);
        return requestDto;
    }

    private Request getRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("ParticipationRequest with id = " + id + " was not found"));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User with id = " + id + " was not found"));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Event with id = " + id + " was not found"));
    }
}
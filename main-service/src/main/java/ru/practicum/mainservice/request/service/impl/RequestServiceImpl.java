package ru.practicum.mainservice.request.service.impl;

import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.State;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.EventLimitException;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.model.RequestStatus;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.request.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.exception.RequestParametersException;
import static ru.practicum.mainservice.config.Constant.EVENT;
import static ru.practicum.mainservice.config.Constant.USER;
import static ru.practicum.mainservice.config.Constant.REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Request create(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT,eventId));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER,userId));

        checkRequestBeforeSave(event, user);
        return requestRepository.save(createRequest(event, user));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Request> getAll(Long userId) {
        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    @Transactional
    public Request cancel(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException(REQUEST,requestId));
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            decreaseConfirmedRequests(request.getEvent());
        }
        request.setStatus(RequestStatus.CANCELED);
        return requestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Request> getRequestsByEventOwner(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException(EVENT,eventId));
        return requestRepository.findAllByEventId(event.getId());
    }

    @Override
    @Transactional
    public Request confirmByEventOwner(Long userId, Long eventId, Long requestId) {
        Request request = requestRepository.findByIdAndEventId(requestId, eventId).orElseThrow(
                () -> new NotFoundException(REQUEST, requestId));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException(EVENT, eventId));
        checkRequestBeforeConfirmation(event);
        request.setStatus(RequestStatus.CONFIRMED);
        request = requestRepository.save(request);
        increaseConfirmedRequests(event);
        checkLimitAndCancelPendingRequests(event);
        return request;
    }

    @Override
    @Transactional
    public Request rejectByEventOwner(Long userId, Long eventId, Long requestId) {
        Request request = requestRepository.findByIdAndEventId(requestId, eventId).orElseThrow(
                () -> new NotFoundException(REQUEST, requestId));
        eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException(EVENT,eventId));
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            decreaseConfirmedRequests(request.getEvent());
        }
        request.setStatus(RequestStatus.REJECTED);
        return requestRepository.save(request);
    }


    private void checkRequestBeforeSave(Event event, User user) {

        if (requestRepository.findFirstByEventIdAndRequesterId(event.getId(), user.getId()).isPresent()) {
            throw new RequestParametersException(String
                    .format("The request for user's participation in tne event id=%d already exists", event.getId()));
        }
        if (event.getInitiator().getId().equals(user.getId())) {
            throw new RequestParametersException("User cannot send participation request in self-published event");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new RequestParametersException("Request can be made only for published events");
        }
        if (event.getParticipantLimit() > 0 && Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            throw new EventLimitException();
        }
    }

    private Request createRequest(Event event, User user) {
        Request request = new Request();
        request.setRequester(user);
        request.setStatus(event.isRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED);
        request.setEvent(request.getStatus().equals(RequestStatus.CONFIRMED) ? increaseConfirmedRequests(event) : event);
        request.setCreatedOn(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        return request;
    }

    private void checkRequestBeforeConfirmation(Event event) {
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new RequestParametersException("The request does not require confirmation");
        }
        if (event.getParticipantLimit() - event.getConfirmedRequests() == 0) {
            throw new EventLimitException();
        }
    }

    private void checkLimitAndCancelPendingRequests(Event event) {
        int availableLimit = event.getParticipantLimit() - event.getConfirmedRequests();
        if (availableLimit == 0) {
            requestRepository.rejectRequestsWithPendingStatus(event.getId());
            log.info("All pending requests were cancelled");
            throw new EventLimitException();
        }
    }

    private Event increaseConfirmedRequests(Event event) {
        int confirmedRequests = event.getConfirmedRequests();
        event.setConfirmedRequests(confirmedRequests + 1);
        return eventRepository.save(event);
    }

    private void decreaseConfirmedRequests(Event event) {
        int confirmedRequests = event.getConfirmedRequests();
        if (confirmedRequests > 0) {
            event.setConfirmedRequests(confirmedRequests - 1);
            eventRepository.save(event);
        }
    }
}
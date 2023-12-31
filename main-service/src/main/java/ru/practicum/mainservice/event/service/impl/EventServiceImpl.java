package ru.practicum.mainservice.event.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Provider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.event.dto.EventUserDto;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.EventAdminDto;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.QEvent;
import ru.practicum.mainservice.event.model.State;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.DataNotFoundException;
import ru.practicum.mainservice.exception.IncorrectConditionException;
import ru.practicum.mainservice.request.dto.RequestDto;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.ViewCountEventRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.model.RequestStatus;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statisticclient.StatClient;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository partReqRepository;
    private final EventMapper mapper;
    private final RequestMapper partReqMapper;
    private final StatClient statsClient;
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Comparator<Event> eventComparator = Comparator.comparing(Event::getPublishedOn);
    private static final int MIN_SIZE_EVENT_LIST = 1;
    private static final long DEFAULT_COUNT_VIEWS = 1L;
    private static final int DEFAULT_COUNT_CONFIRMED_REQUESTS = 0;
    private static final LocalDateTime MAX_DATE_IN_FUTURE =
            LocalDateTime.of(2030, 12, 31, 0, 0, 0);
    private static final int PLACE_EVENT_NUMBER_IN_ADDRESS = "/events/".length();

    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectConditionException("Дата и время на которые намечено событие(" +
                    newEventDto.getEventDate().toString() + ") не может быть раньше," +
                    " чем через два часа от текущего момента");
        }
        Event event = mapper.mapToEventFromEventAdminDto(newEventDto);
        event.setInitiator(getUser(userId));
        event.setCategory(getCategory(newEventDto.getCategory()));
        event.setState(State.PENDING);
        event.setCreatedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        EventFullDto eventFullDto = mapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
        log.info("Добавлено новое событие {}", eventFullDto);
        return insertCountConfirmedRequests(eventFullDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> findAllUserEvents(Long userId, PageRequest page) {
        getUser(userId);
        List<EventShortDto> events = eventRepository.findEventsByInitiatorId(userId, page)
                .stream()
                .map(mapper::mapToEventShortDtoFromEvent)
                .collect(Collectors.toList());
        log.info("Список событий добавленных пользователем {}: {}", userId, events);
        return events;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto findUserEvent(Long userId, Long eventId) {
        getUser(userId);
        Event event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataNotFoundException("Event with id = " + eventId + " was not found");
        }
        EventFullDto eventFullDto = mapper.mapToEventFullDtoFromEvent(event);
        log.info("Пользователем {} запрошенно событие {}", userId, eventFullDto);
        return insertCountConfirmedRequests(eventFullDto);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, EventUserDto updateEvent) {
        Event event = getEvent(eventId);
        validationUpdateEventByInitiator(userId, event, updateEvent);
        Provider<Event> eventProvider = p -> event;
        mapper.getPropertyUpdEventUserReq().setProvider(eventProvider);
        Event actualEvent = mapper.mapToEventFromEventAdminDto(updateEvent);
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(EventUserDto.StateAction.CANCEL_REVIEW)) {
                actualEvent.setState(State.CANCELED);
            } else {
                actualEvent.setState(State.PENDING);
            }
        }
        EventFullDto eventFullDto = mapper.mapToEventFullDtoFromEvent(eventRepository.save(actualEvent));
        log.info("Пользователем {} обновлено событие {}", userId, eventFullDto);
        return insertCountConfirmedRequests(eventFullDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getInfoAboutRequests(Long userId, Long eventId) {
        List<RequestDto> requests =
                partReqRepository.findAllByEventInitiatorIdAndEventId(userId, eventId)
                        .stream()
                        .map(partReqMapper::toDto)
                        .collect(Collectors.toList());
        log.info("Инициатором события {} получена информация о запросах на участие {}", eventId, requests);
        return requests;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        Event event = getEvent(eventId);
        int countConfirmedRequests = partReqRepository.calculateConfirmedRequestsEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь " + userId + " не является инициатором события " + eventId);
        }
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            throw new ConflictException("The number of participants is limited or pre-moderation is disabled");
        }
        if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED) &&
                event.getParticipantLimit() - countConfirmedRequests == 0) {
            throw new ConflictException("Попытка принять заявку на участие в событии, когда лимит уже достигнут");
        }
        return changeRequestStatus(event, updateRequest);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, EventAdminDto updEventReq) {
        Event event = getEvent(eventId);
        Provider<Event> eventProvider = p -> event;
        mapper.getPropertyUpdEventAdminReq().setProvider(eventProvider);
        Event actualEvent = mapper.mapToEventFromEventAdminDto(updEventReq);

        if (updEventReq.getStateAction() != null) {
            validationUpdateEventByAdmin(event, updEventReq);
            if (updEventReq.getStateAction().equals(EventAdminDto.StateAction.PUBLISH_EVENT)) {
                actualEvent.setState(State.PUBLISHED);
                actualEvent.setPublishedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                actualEvent.setAvailable(true);
            } else {
                actualEvent.setState(State.CANCELED);
                actualEvent.setAvailable(false);
            }
        }
        EventFullDto eventFullDto = mapper.mapToEventFullDtoFromEvent(eventRepository.save(actualEvent));
        log.info("Администратором обновлено событие {}", eventFullDto);
        return insertCountConfirmedRequests(eventFullDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsForAdmin(List<Long> users, List<State> states, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        Predicate predicate = conditionForAdminFilter(users, states, categories, rangeStart, rangeEnd).getValue();
        assert predicate != null;
        List<Event> events = eventRepository.findAll(predicate, pageable).getContent();
        List<EventFullDto> completedEvents = completeEvents(events);
        log.info("Администратором получен список событий {}", completedEvents);
        return completedEvents;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsForPublic(String text, List<Long> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable, String sort, int from, int size,
                                                   HttpServletRequest request) {
        Pageable pageable;
        statsClient.addHit(request);
        Predicate predicate = conditionForUserFilter(text, categories,
                paid, rangeStart, rangeEnd, onlyAvailable).getValue();

        if (sort.equals("EVENT_DATE")) {
            pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        } else {
            pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "views"));
        }
        assert predicate != null;
        List<Event> events = eventRepository.findAll(predicate, pageable).getContent();
        List<EventFullDto> completedEvents = completeEvents(events);
        log.info("По публичному эндпоинту получен список событий {}", completedEvents);
        return completedEvents;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto findEventById(Long id, HttpServletRequest request) {
        Event event = getEvent(id);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new DataNotFoundException("Event with id = " + id + " was not found");
        }
        statsClient.addHit(request);
        Event actualEvent = getEventWithStat(List.of(event)).get(0);

        EventFullDto eventFullDto = mapper.mapToEventFullDtoFromEvent(eventRepository.save(actualEvent));
        log.info("Запрошенно событие {}", eventFullDto);
        return insertCountConfirmedRequests(eventFullDto);
    }

    private EventRequestStatusUpdateResult changeRequestStatus(Event event,
                                                               EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<Request> requests = partReqRepository.findAllById(updateRequest.getRequestIds());
        List<Event> confirmedRequests = new ArrayList<>();
        requests.forEach(request -> {
            int countConfirmedRequests = partReqRepository.calculateConfirmedRequestsEvent(event.getId());
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Request must have status PENDING");
            }
            if (event.getParticipantLimit() - countConfirmedRequests == 0) {
                updateRequest.setStatus(RequestStatus.REJECTED);
            }
            request.setStatus(updateRequest.getStatus());
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                confirmedRequests.add(event);
            }
            partReqRepository.save(request);
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                result.setConfirmedRequests(List.of(partReqMapper.toDto(request)));
            } else {
                result.setRejectedRequests(List.of(partReqMapper.toDto(request)));
            }
        });
        eventRepository.saveAll(confirmedRequests);
        log.info("Инициатором события обновлены статусы запросов на участие {}", result);
        return result;
    }

    private BooleanBuilder getConditions(List<Long> categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd) {
        QEvent qEvent = QEvent.event;
        BooleanBuilder conditions = new BooleanBuilder();
        if (categories != null && !categories.isEmpty()) {
            conditions.and(QEvent.event.category.id.in(categories));
        }
        conditions.and(qEvent.eventDate.after(Objects.requireNonNullElseGet(rangeStart, LocalDateTime::now)));
        if (rangeEnd != null) {
            conditions.and(qEvent.eventDate.before(rangeEnd));
        } else {
            rangeEnd = MAX_DATE_IN_FUTURE;
            conditions.and(qEvent.eventDate.before(rangeEnd));
        }
        return conditions;
    }

    private BooleanBuilder conditionForAdminFilter(List<Long> users, List<State> states,
                                                   List<Long> categories, LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd) {
        BooleanBuilder conditions = getConditions(categories, rangeStart, rangeEnd);
        if (users != null && !users.isEmpty()) {
            conditions.and(QEvent.event.initiator.id.in(users));
        }
        if (states != null) {
            conditions.and(QEvent.event.state.in(states));
        }
        return conditions;
    }

    private BooleanBuilder conditionForUserFilter(String text, List<Long> categories, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable) {
        BooleanBuilder conditions = getConditions(categories, rangeStart, rangeEnd);
        conditions.and(QEvent.event.state.eq(State.PUBLISHED));
        if (text != null) {
            conditions.and(QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text)));
        }
        if (paid != null) {
            conditions.and(QEvent.event.paid.eq(paid));
        }
        if (onlyAvailable != null) {
            conditions.and(QEvent.event.available.eq(true));
        }
        return conditions;
    }

    private void validationUpdateEventByAdmin(Event event, EventAdminDto updEventReq) {
        if (updEventReq.getStateAction().equals(EventAdminDto.StateAction.PUBLISH_EVENT) &&
                event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IncorrectConditionException("Дата начала изменяемого события должна быть не ранее" +
                    " чем за час от даты публикации" + event.getEventDate().toString());
        }
        if ((updEventReq.getStateAction().equals(EventAdminDto.StateAction.PUBLISH_EVENT)
                && !event.getState().equals(State.PENDING)) ||
                (updEventReq.getStateAction().equals(EventAdminDto.StateAction.REJECT_EVENT)
                        && event.getState().equals(State.PUBLISHED))) {
            throw new ConflictException("Cannot publish the event because it's not in the right state:" +
                    " PUBLISHED");
        }
    }

    private void validationUpdateEventByInitiator(Long userId, Event event, EventUserDto updateEvent) {
        getUser(userId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataNotFoundException("Event with id=" + event.getId() + " was not found");
        }

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateEvent.getEventDate() != null && updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectConditionException("Дата и время на которые намечено событие(" +
                    updateEvent.getEventDate().toString() + ") не может быть раньше," +
                    " чем через два часа от текущего момента");
        }
    }

    private List<EventFullDto> completeEvents(List<Event> events) {
        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());

        List<Event> notPublishedEvent = events.stream()
                .filter(event -> event.getPublishedOn() == null)
                .collect(Collectors.toList());

        List<Event> eventsWithStat = getEventWithStat(publishedEvents);

        List<Event> allEvents = new ArrayList<>();
        allEvents.addAll(notPublishedEvent);
        allEvents.addAll(insertCountConfirmedRequests(eventsWithStat));

        return allEvents.stream()
                .map(mapper::mapToEventFullDtoFromEvent)
                .collect(Collectors.toList());
    }

    private List<Event> getEventWithStat(List<Event> events) {
        Map<Long, Long> eventViews = new HashMap<>();
        if (events.size() > MIN_SIZE_EVENT_LIST) {
           events.sort(eventComparator);
        }
        LocalDateTime earliestDate = events.get(0).getPublishedOn();
        List<String> url = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        List<ViewStatsDto> stat = statsClient.getStat(earliestDate.format(formatter),
                LocalDateTime.now().format(formatter), url, true).getBody();

        assert stat != null;
        for (ViewStatsDto line : stat) {
            Long id = Long.parseLong(line.getUri().substring(PLACE_EVENT_NUMBER_IN_ADDRESS));
            eventViews.put(id, line.getHits());
        }

        for (Event event : events) {
            event.setViews(eventViews.getOrDefault(event.getId(), DEFAULT_COUNT_VIEWS));
        }
        return events;
    }

    private EventFullDto insertCountConfirmedRequests(EventFullDto event) {
        event.setConfirmedRequests(partReqRepository.calculateConfirmedRequestsEvent(event.getId()));
        return event;
    }

    private List<Event> insertCountConfirmedRequests(List<Event> events) {
        List<Long> ids = events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Integer> countedEventRequests = partReqRepository.calculateConfirmedRequestsEvents(ids).stream()
                .collect(Collectors.toMap(
                        ViewCountEventRequest::getEventId,
                        ViewCountEventRequest::getCountEventRequests)
                );

        for (Event event : events) {
            event.setConfirmedRequests(countedEventRequests
                    .getOrDefault(event.getId(), DEFAULT_COUNT_CONFIRMED_REQUESTS));
        }
        return events;
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("Event with id = %d was not found", id)));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("User with id = %d was not found", id)));
    }

    private Category getCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Category with id = %d was not found", catId)));
    }
}
package ru.practicum.mainservice.event.service.impl;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.event.dto.AdminUpdateEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventRequest;
import ru.practicum.mainservice.event.dto.SearchParameters;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.State;
import ru.practicum.mainservice.event.model.QEvent;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.event.service.EventService;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.exception.EventDateException;
import ru.practicum.mainservice.exception.EventStateException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.exception.UserAccessRightsException;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.statdto.ViewStatsDto;
import ru.practicum.statisticclient.StatClient;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static ru.practicum.mainservice.config.Constant.USER;
import static ru.practicum.mainservice.config.Constant.EVENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final boolean UNIQUE_VIEWS = false;

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StatClient client = new StatClient("http://stat-server:9090", new RestTemplateBuilder());

    @Override
    @Transactional
    public Collection<Event> search(SearchParameters params) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder(event.state.eq(State.PUBLISHED));
        if (params.getText() != null) {
            predicate.and(event.annotation.containsIgnoreCase(params.getText()))
                    .or(event.description.containsIgnoreCase(params.getText()));
        }
        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            predicate.and(event.category.id.in(params.getCategories()));
        }
        if (params.getPaid() != null) {
            predicate.and(event.paid.eq(params.getPaid()));
        }
        predicate.and(event.eventDate.goe(params.getRangeStart() != null ? params.getRangeStart() : LocalDateTime.now()));

        if (params.getRangeStart() != null) {
            predicate.and(event.eventDate.loe(params.getRangeEnd()));
        }
        if (params.isOnlyAvailable()) {
            predicate.and(event.confirmedRequests.gt(0)).and(event.confirmedRequests.loe(event.participantLimit));
        }
        Sort sort = Sort.by(Sort.Direction.DESC, params.getSort().getValue());
        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), sort);

        client.addHit(params.getRequest());

        return eventRepository.findAll(predicate, pageable).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Event> getAllAdmin(SearchParameters params) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder();
        if (params.getUsers() != null && !params.getUsers().isEmpty()) {
            predicate.and(event.initiator.id.in(params.getUsers()));
        }
        if (params.getStates() != null && !params.getStates().isEmpty()) {
            predicate.and(event.state.in(params.getStates().stream().map(s -> State.valueOf(s.toUpperCase())).collect(Collectors.toList())));
        }
        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            predicate.and(event.category.id.in(params.getCategories()));
        }
        predicate.and(event.eventDate.goe(params.getRangeStart() != null ? params.getRangeStart() : LocalDateTime.now()));

        if (params.getRangeStart() != null) {
            predicate.and(event.eventDate.loe(params.getRangeEnd()));
        }
        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());
        return eventRepository.findAll(predicate, pageable).toList();
    }

    @Override
    @Transactional
    public Event getById(Long eventId, HttpServletRequest request) {
        Long views = 0L;
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED).orElseThrow(
                () -> new NotFoundException(EVENT, eventId));
        try {
            client.addHit(request);
        } catch (Exception exception) {
            log.error("An error {} occurred while sending statistics to stats server", exception.getMessage());
        }
        views = getViews(event, new String[]{request.getRequestURI()});
        event.setViews(views);
        eventRepository.save(event);
        return event;
    }

    private Long getViews(Event event, String [] uri) {
        ResponseEntity<List<ViewStatsDto>> response = client.getStat(event.getPublishedOn().toString(),
                LocalDateTime.now().toString(), uri,UNIQUE_VIEWS);
        if (response != null) {
            return Objects.requireNonNull(response.getBody()).get(0).getHits();
        }
        return 0L;
    }

    @Override
    @Transactional
    public Event create(NewEventDto newEventDto, Long userId) {
        Event event = eventMapper.convert(newEventDto);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER, userId));
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        return eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Event getByOwner(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException(EVENT, eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Event> getAllByOwner(Long userId, Pageable pageable) {
        return eventRepository.findAllByInitiatorId(userId, pageable).toList();
    }

    @Override
    @Transactional
    public Event update(long userId, UpdateEventRequest eventRequest) {
        Event event = eventRepository.findById(eventRequest.getEventId()).orElseThrow(
                () -> new NotFoundException(EVENT,eventRequest.getEventId()));
        checkEventBeforeUpdate(event, userId);
        event.setState(State.PENDING);
        eventMapper.updateFromEventDto(eventRequest, event);
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event updateAdmin(AdminUpdateEventDto eventDto, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT,eventId));
        return eventRepository.save(eventMapper.updateFromAdminDto(eventDto, event));
    }

    @Override
    @Transactional
    public Event publish(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT,eventId));
        checkEventBeforePublish(event);
        event.setPublishedOn(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        event.setState(State.PUBLISHED);
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event reject(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT,eventId));
        if (!event.getState().equals(State.PENDING)) {
            throw new EventStateException(event.getState().toString());
        }
        event.setState(State.CANCELED);
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event cancel(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT,eventId));
        checkEventBeforeCancel(event, userId);
        event.setState(State.CANCELED);
        return eventRepository.save(event);
    }

    private void checkEventBeforeUpdate(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new UserAccessRightsException();
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new EventStateException(event.getState().toString());
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventDateException(2, "changing");
        }
    }

    private void checkEventBeforeCancel(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new UserAccessRightsException();
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new EventStateException(event.getState().toString());
        }
    }

    private void checkEventBeforePublish(Event event) {
        if (!event.getState().equals(State.PENDING)) {
            throw new EventStateException(event.getState().toString());
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1).plusSeconds(30))) {
            throw new EventDateException(1, "publishing");
        }
    }
}
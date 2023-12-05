package ru.practicum.ewm.server.service.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.server.service.event.model.Event;
import org.springframework.data.domain.PageRequest;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Page<Event> findEventsByInitiatorId(Long userId, PageRequest page);

    List<Event> findAllByCategory_Id(Long id);
}
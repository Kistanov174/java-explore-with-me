package ru.practicum.mainservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventInitiatorIdAndEventId(Long initiatorId, Long eventId);
}
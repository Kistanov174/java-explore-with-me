package ru.practicum.mainservice.request.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.request.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Object findByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByUserIdAndEventId(Long userId, Long eventId);
}
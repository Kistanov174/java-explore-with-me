package ru.practicum.mainservice.request.repository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.request.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findFirstByEventIdAndRequesterId(Long eventId, Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    Optional<Request> findByIdAndEventId(Long requestId, Long eventId);

    Collection<Request> findAllByRequesterId(Long userId);

    Collection<Request> findAllByEventId(Long eventId);

    @Modifying
    @Query("update Request r " +
            "  set r.status = 'REJECT' " +
            "where r.event.id = :eventId " +
            "      and r.status = 'PENDING'")
    void rejectRequestsWithPendingStatus(Long eventId);
}
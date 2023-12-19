package ru.practicum.mainservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.mainservice.request.dto.ViewCountEventRequest;
import ru.practicum.mainservice.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventInitiatorIdAndEventId(Long initiatorId, Long eventId);

    @Query(value = "SELECT COUNT(id) " +
                   "FROM requests " +
                   "WHERE event_id = :event_id AND status = 'CONFIRMED'", nativeQuery = true)
    Integer calculateConfirmedRequestsEvent(@Param("event_id") Long eventId);

    @Query("SELECT r.event.id AS eventId, COUNT(r.id) AS countEventRequests " +
            "FROM Request r " +
            "WHERE r.event.id IN :eventIds AND (r.status = 'CONFIRMED') " +
            "GROUP BY r.event.id")
    List<ViewCountEventRequest> calculateConfirmedRequestsEvents(@Param("eventIds") List<Long> eventId);
}
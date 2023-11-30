package ru.practicum.mainservice.request.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.request.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Object findByRequester_IdAndEvent_Id(Long requesterId, Long eventId);

    List<Request> findAllByRequester_Id(Long requesterId);

    List<Request> findAllByEvent_Initiator_IdAndEvent_Id(Long initiatorId, Long eventId);
}
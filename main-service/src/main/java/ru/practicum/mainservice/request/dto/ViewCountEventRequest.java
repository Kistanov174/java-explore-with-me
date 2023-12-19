package ru.practicum.mainservice.request.dto;

public interface ViewCountEventRequest {
    Long getEventId();

    Integer getCountEventRequests();
}
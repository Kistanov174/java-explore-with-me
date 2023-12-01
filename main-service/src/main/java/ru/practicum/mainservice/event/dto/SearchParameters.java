package ru.practicum.mainservice.event.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.mainservice.event.model.SortBy;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SearchParameters {
    private String text;
    private List<Long> users;
    private List<String> states;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private boolean onlyAvailable;
    private int page;
    private int size;
    private SortBy sort;
    private HttpServletRequest request;

    @Override
    public String toString() {
        return "SearchParameters{" +
                "text='" + text + '\'' +
                ", categories=" + categories +
                ", paid=" + paid +
                ", rangeStart=" + rangeStart +
                ", rangeEnd=" + rangeEnd +
                ", onlyAvailable=" + onlyAvailable +
                ", page=" + page +
                ", size=" + size +
                ", sort=" + sort +
                '}';
    }
}
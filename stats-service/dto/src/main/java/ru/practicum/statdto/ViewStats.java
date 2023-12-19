package ru.practicum.statdto;

public interface ViewStats {
    String getApp();

    void setApp(String app);

    String getUri();

    void setUri(String uri);

    Long getHits();

    void setHits(Long hits);
}
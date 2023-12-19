package ru.practicum.statisticclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statdto.EndpointHitDto;
import ru.practicum.statdto.ViewStatsDto;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Service
public class StatClient {
    private final RestTemplate template;
    private final String url;

    public StatClient(@Value("${STAT_SERVER_URL}") String url,
                      RestTemplateBuilder template) {
        this.url = url;
        this.template = template
                .uriTemplateHandler(new DefaultUriBuilderFactory(this.url))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    @PostConstruct
    public void logUrl() {
        log.info("Url to stats server: {}", url);
    }

    public void addHit(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                null,
                "main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                null
        );
        template.postForEntity("/hit",
                new HttpEntity<>(endpointHitDto),
                EndpointHitDto.class);
    }

    public ResponseEntity<List<ViewStatsDto>> getStat(String start,
                                                      String end,
                                                      List<String> uris,
                                                      boolean unique) {
        return template.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                },
                start, end, uris, unique);
    }

    private <T> HttpEntity<T> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }
}
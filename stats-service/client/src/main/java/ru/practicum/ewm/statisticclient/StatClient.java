package ru.practicum.ewm.statisticclient;

import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.statdto.EndpointHitDto;
import ru.practicum.ewm.statdto.ViewStatsDto;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class StatClient {
    @Value("${SERVER_URL}")String url;
//    private final RestTemplate template;
//    public StatClient(@Value("${SERVER_URL}")String url,
//                      RestTemplateBuilder templateBuilder) {
//        this.template = templateBuilder
//                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
//                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
//                .build();
//    }

    public void addHit(HttpServletRequest request) {
        RestTemplate template = new RestTemplate();
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                null,
                "main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                null
        );
        template.postForEntity(url + "/hit",
                new HttpEntity<>(endpointHitDto),
                EndpointHitDto.class);
    }

    public ResponseEntity<List<ViewStatsDto>> getStat(String start,
                                                      String end,
                                                      String[] uris,
                                                      boolean unique) {
        RestTemplate template = new RestTemplate();
        return template.exchange(url + "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
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
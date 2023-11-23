package ru.practicum.statdto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class ViewStatsDto implements ViewStats {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    private Long hits;
}

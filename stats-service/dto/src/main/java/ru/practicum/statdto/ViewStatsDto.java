package ru.practicum.statdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto implements ViewStats {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    private Long hits;
}
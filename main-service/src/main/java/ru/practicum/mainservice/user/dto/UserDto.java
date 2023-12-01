package ru.practicum.mainservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.mainservice.config.Create;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Поле name не должно быть пустым", groups = {Create.class})
    private String name;

    @Email(groups = {Create.class})
    @NotBlank(message = "Поле email не должно быть пустым", groups = {Create.class})
    private String email;
}
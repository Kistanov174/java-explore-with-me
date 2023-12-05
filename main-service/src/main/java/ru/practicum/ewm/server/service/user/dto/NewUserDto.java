package ru.practicum.ewm.server.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {
    @NotBlank
    @Length(min = 2, max = 250)
    private String name;
    @Email
    @NotBlank
    @Length(min = 6, max = 254)
    private String email;
}
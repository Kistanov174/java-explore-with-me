package ru.practicum.mainservice.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.dto.UserShortDto;
import ru.practicum.mainservice.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto convertToDto(User user);

    UserShortDto convertToShort(User user);

    User convert(UserDto userDto);
}
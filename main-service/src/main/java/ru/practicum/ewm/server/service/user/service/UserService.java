package ru.practicum.ewm.server.service.user.service;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.server.service.user.dto.NewUserDto;
import ru.practicum.ewm.server.service.user.dto.UserDto;


import java.util.List;

public interface UserService {
    UserDto addUser(NewUserDto newUserDto) throws ConstraintViolationException;

    boolean deleteUser(Long id);

    List<UserDto> findUsers(List<Long> ids, PageRequest pageRequest);
}
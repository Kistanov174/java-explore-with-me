package ru.practicum.mainservice.user.service.userserviceimpl;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.mainservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User create(UserDto userDto) {
        User user = userMapper.convert(userDto);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> getUsers(Long[] ids, Pageable pageable) {
        if (ids == null) {
            return userRepository.findAll(pageable).toList();
        }
        return userRepository.findAllByIdIn(Arrays.asList(ids), pageable).toList();
    }
}
package ru.practicum.ewm.server.service.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.server.service.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Object findUserByEmail(String email);
}

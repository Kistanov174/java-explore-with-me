package ru.practicum.mainservice.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.comment.model.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(long eventId);
}
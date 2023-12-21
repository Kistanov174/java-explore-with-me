package ru.practicum.mainservice.comment.service.impl;

import ru.practicum.mainservice.comment.dto.CommentDto;
import ru.practicum.mainservice.comment.dto.NewCommentDto;
import ru.practicum.mainservice.comment.dto.UpdateCommentRequest;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.repository.CommentRepository;
import ru.practicum.mainservice.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.DataNotFoundException;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public List<CommentDto> getAllByEventId(long eventId) {
        return commentRepository.findAllByEventId(eventId).stream()
                .map(commentMapper::mapToCommentDtoFromComment)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto createComment(long userId, long eventId, NewCommentDto dto) {
        User user = findUserById(userId);
        Event event = findEventById(eventId);

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setEvent(event);
        comment.setText(dto.getText());
        comment.setCreatedOn(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return commentMapper.mapToCommentDtoFromComment(comment);
    }

    @Transactional
    public CommentDto updateByUser(long userId, long commentId, UpdateCommentRequest updateRequest) {
        findUserById(userId);
        Comment comment = findCommentById(commentId);

        if (!getUserIdFromComment(comment).equals(userId)) {
            throw new ConflictException(String.format("User id = %d not owner of Comment id = %d", userId, commentId));
        }

        Optional.ofNullable(updateRequest.getText()).ifPresent(comment::setText);
        return commentMapper.mapToCommentDtoFromComment(comment);
    }

    @Transactional
    public CommentDto updateByAdmin(long commentId, UpdateCommentRequest updateRequest) {
        Comment comment = findCommentById(commentId);
        Optional.ofNullable(updateRequest.getText()).ifPresent(comment::setText);
        return commentMapper.mapToCommentDtoFromComment(comment);
    }

    public void deleteByUser(long userId, long commentId) {
        findUserById(userId);
        Comment comment = findCommentById(commentId);

        if (!getUserIdFromComment(comment).equals(userId)) {
            throw new ConflictException(String.format("User id = %d not owner of Comment id = %d", userId, commentId));
        }

        commentRepository.deleteById(commentId);
    }

    public void deleteByAdmin(long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Long getUserIdFromComment(Comment comment) {
        Long id;
        try {
            id = comment.getUser().getId();
        } catch (NullPointerException e) {
            id = 0L;
        }
        return id;
    }

    private Comment findCommentById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("Comment with id = %d was not found", id)));
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("User with id = %d was not found", id)));
    }

    private Event findEventById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("Event with id = %d was not found", id)));
    }
}
package ru.practicum.mainservice.comment.service;

import ru.practicum.mainservice.comment.dto.CommentDto;
import ru.practicum.mainservice.comment.dto.NewCommentDto;
import ru.practicum.mainservice.comment.dto.UpdateCommentRequest;
import java.util.List;

public interface CommentService {
    List<CommentDto> getAllByEventId(long eventId);

    CommentDto createComment(long userId, long eventId, NewCommentDto dto);

    CommentDto updateByUser(long userId, long commentId, UpdateCommentRequest updateRequest);

    CommentDto updateByAdmin(long commentId, UpdateCommentRequest updateRequest);

    void deleteByUser(long userId, long commentId);

    void deleteByAdmin(long commentId);
}
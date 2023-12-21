package ru.practicum.mainservice.comment.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.comment.dto.CommentDto;
import ru.practicum.mainservice.comment.model.Comment;

@Component
public class CommentMapper {
    private final ModelMapper modelMapper;

    public CommentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.createTypeMap(Comment.class, CommentDto.class);
    }

    public CommentDto mapToCommentDtoFromComment(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }
}
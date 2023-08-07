package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDto commentToCommentDto(Comment comment);


}

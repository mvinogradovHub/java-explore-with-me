package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.model.Comment;

public interface CommentService {

    void deleteCommentByAdmin(Long comId, Long eventId);

    CommentDto editCommentByAdmin(Long comId, Long eventId, Comment comment);

}

package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CommentService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public void deleteCommentByAdmin(Long comId, Long eventId) {
        commentRepository.findById(comId).orElseThrow(() -> new NotFoundException("Comment with id=" + comId + " was not found"));
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        commentRepository.deleteById(comId);
    }

    @Override
    public CommentDto editCommentByAdmin(Long comId, Long eventId, Comment comment) {
        Comment commentInRepo = commentRepository.findById(comId).orElseThrow(() -> new NotFoundException("Comment with id=" + comId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        commentInRepo.setId(comId);
        commentInRepo.setEvent(event);
        commentInRepo.setText(comment.getText());
        commentInRepo.setUpdated(LocalDateTime.now());
        return commentMapper.commentToCommentDto(commentRepository.save(commentInRepo));
    }

}

package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {
    private final EventService eventService;
    private final CommentService commentService;


    @GetMapping
    public List<EventFullPrivateDto> searchEvents(@RequestParam(required = false) List<Long> users,
                                                  @RequestParam(required = false) List<EventState> states,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received request to GET /admin/events?users={}states={}&categories={}&rangeStart={}&rangeEnd={}&from={}&size={}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsByAdmin(EventSearchAdminRequestDto.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build());
    }


    @PatchMapping("/{eventId}")
    public EventFullPrivateDto updateEvent(@PathVariable Long eventId, @RequestBody @Valid EventUpdateByAdminDto event) {
        log.info("Received request to PATCH /admin/events/{} with body: {}", eventId, event);
        return eventService.updateEventByAdmin(eventId, event);
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long eventId, @PathVariable Long commentId, @RequestBody @Valid Comment comment) {
        log.info("Received request to PATCH /admin/events/{}/comments/{} with body: {}", eventId, commentId, comment);
        return commentService.editCommentByAdmin(commentId, eventId, comment);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long eventId, @PathVariable Long commentId) {
        log.info("Received request to DELETE /admin/events/{}/comments/{}", eventId, commentId);
        commentService.deleteCommentByAdmin(commentId, eventId);
    }

    @PatchMapping("/{eventId}/moderation")
    public EventFullPrivateDto moderationEvent(@PathVariable Long eventId, @RequestBody @Valid EventModerationByAdminDto moderation) {
        log.info("Received request to PATCH /admin/events/{}/moderation with body: {}", eventId, moderation);
        return eventService.changeModerationStatusByAdmin(eventId, moderation);
    }

}

package ru.practicum.ewm.controller.privately;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody @Valid EventNewDto event) {
        log.info("Received request to POST /users/{}/events with body: {}", userId, event);
        return eventService.addEventByUser(userId, event);
    }


    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received request to GET /users/{}/events?from={}&size={}", userId, from, size);
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullPrivateDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Received request to GET /users/{}/events/{}", userId, eventId);
        return eventService.getEventByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullPrivateDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid EventUpdateByUserDto event) {
        log.info("Received request to PATCH /users/{}/events/{} with body: {}", eventId, userId, event);
        return eventService.updateEventByUser(userId, eventId, event);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Received request to GET /users/{}/events/{}/requests", userId, eventId);
        return requestService.getRequestByUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResultDto requestsStatusChange(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody RequestStatusUpdateDto updateDto) {
        log.info("Received request to PATCH /users/{}/events/{}/requests with body: {}", eventId, userId, updateDto);
        return requestService.changeStatusRequestsByUser(userId, eventId, updateDto);
    }


}

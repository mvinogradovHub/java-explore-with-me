package ru.practicum.ewm.controller.privately;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.EventStateUserAction;
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
        return eventService.addUserEvent(userId, event);
    }


    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received request to GET /users/{}/events?from={}&size={}", userId, from, size);
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Received request to GET /users/{}/events/{}", userId, eventId);
        return eventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid EventUpdateDto<EventStateUserAction> event) {
        log.info("Received request to PATCH /users/{}/events/{} with body: {}", eventId, userId, event);
        return eventService.updateUserEvent(userId, eventId, event);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getUserEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Received request to GET /users/{}/events/{}/requests", userId, eventId);
        return requestService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResultDto requestsStatusChange(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody RequestStatusUpdateDto updateDto) {
        log.info("Received request to PATCH /users/{}/events/{}/requests with body: {}", eventId, userId, updateDto);
        return requestService.requestsStatusChange(userId, eventId, updateDto);
    }
}

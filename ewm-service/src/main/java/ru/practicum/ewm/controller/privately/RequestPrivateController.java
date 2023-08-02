package ru.practicum.ewm.controller.privately;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.service.RequestService;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestPrivateController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Received request to POST /users/{}/requests?eventId={}", userId, eventId);
        return requestService.addRequestByUser(userId, eventId);
    }


    @GetMapping
    public List<RequestDto> getEvents(@PathVariable Long userId) {
        log.info("Received request to GET /users/{}/requests", userId);
        return requestService.getRequestsByUser(userId);
    }


    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelUserRequests(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Received request to PATCH GET /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.cancelRequestByUser(userId, requestId);
    }
}

package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.dto.RequestStatusUpdateResultDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.RequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;


    @Override
    public RequestDto addRequestByUser(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));


        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ValidationException("the initiator of the event cannot add a request to participate in his event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("you cannot participate in an unpublished event");
        }

        List<Request> requests = requestRepository.findByEventIdAndStatusIn(eventId, List.of(RequestStatus.CONFIRMED));

        if (event.getParticipantLimit() != 0 && requests.size() >= event.getParticipantLimit()) {
            throw new ValidationException("the limit of participation requests has been reached");
        }

        Request request = Request.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .event(event)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        eventRepository.save(event);
        return requestMapper.requestToRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getRequestsByUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        List<Request> requests = requestRepository.findByRequesterId(userId);
        return requests.stream().map(requestMapper::requestToRequestDto).collect(Collectors.toList());
    }

    @Override
    public RequestDto cancelRequestByUser(Long userId, Long requestId) {
        Request request = requestRepository.findByRequesterIdAndId(userId, requestId).orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        Event event = eventRepository.findById(request.getEvent().getId()).orElseThrow(() -> new NotFoundException("Event with id=" + request.getEvent().getId() + " was not found"));
        request.setStatus(RequestStatus.CANCELED);
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        eventRepository.save(event);
        return requestMapper.requestToRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getRequestByUserEvent(Long userId, Long eventId) {
        List<Request> requests = requestRepository.findByEventInitiatorIdAndEventId(userId, eventId);
        return requests.stream().map(requestMapper::requestToRequestDto).collect(Collectors.toList());
    }

    @Override
    public RequestStatusUpdateResultDto ChangeStatusRequestsByUser(Long userId, Long eventId, RequestStatusUpdateDto updateDto) {

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getRequestModeration() || event.getParticipantLimit() > 0) {
            List<Request> requests = requestRepository.findByEventInitiatorIdAndEventIdAndStatusIn(userId, eventId, List.of(RequestStatus.PENDING));

            long requestsConfirmed = event.getConfirmedRequests();

            if (event.getParticipantLimit() != 0 && requestsConfirmed >= event.getParticipantLimit()) {
                throw new ValidationException("the limit of participation requests has been reached");
            }

            List<Request> resultConformedRequestList = new ArrayList<>();
            List<Request> resultCanceledRequestList = new ArrayList<>();

            for (Request request : requests) {
                if (updateDto.getRequestIds().contains(request.getId())) {
                    if ((requestsConfirmed < event.getParticipantLimit()) && updateDto.getStatus().equals(RequestStatus.CONFIRMED)) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        requestRepository.save(request);
                        eventRepository.save(event);
                        requestsConfirmed++;
                        resultConformedRequestList.add(request);
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
                        requestRepository.save(request);
                        eventRepository.save(event);
                        resultCanceledRequestList.add(request);
                    }

                }
            }

            return RequestStatusUpdateResultDto.builder()
                    .confirmedRequests(resultConformedRequestList.stream()
                            .map(requestMapper::requestToRequestDto)
                            .collect(Collectors.toList()))
                    .rejectedRequests(resultCanceledRequestList.stream()
                            .map(requestMapper::requestToRequestDto)
                            .collect(Collectors.toList()))
                    .build();

        } else {
            return RequestStatusUpdateResultDto.builder().build();
        }
    }


}

package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.dto.RequestStatusUpdateResultDto;

import java.util.List;

public interface RequestService {
    RequestDto addUserRequest(Long userId, Long eventId);

    List<RequestDto> getUserRequests(Long userId);

    RequestDto cancelUserRequests(Long userId, Long requestId);

    List<RequestDto> getUserEventRequests(Long userId, Long eventId);

    RequestStatusUpdateResultDto requestsStatusChange(Long userId, Long eventId, RequestStatusUpdateDto updateDto);


}

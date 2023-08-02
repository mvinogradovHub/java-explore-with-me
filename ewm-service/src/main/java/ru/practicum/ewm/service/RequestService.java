package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.dto.RequestStatusUpdateResultDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequestByUser(Long userId, Long eventId);

    List<RequestDto> getRequestsByUser(Long userId);

    RequestDto cancelRequestByUser(Long userId, Long requestId);

    List<RequestDto> getRequestByUserEvent(Long userId, Long eventId);

    RequestStatusUpdateResultDto ChangeStatusRequestsByUser(Long userId, Long eventId, RequestStatusUpdateDto updateDto);


}

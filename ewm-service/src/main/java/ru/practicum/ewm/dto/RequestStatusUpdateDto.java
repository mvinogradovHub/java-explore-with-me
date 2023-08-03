package ru.practicum.ewm.dto;

import lombok.*;
import ru.practicum.ewm.model.RequestStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RequestStatusUpdateDto {
    @NotNull
    private List<Long> requestIds;

    @NotBlank
    private RequestStatus status;
}

package ru.practicum.ewm.dto;

import lombok.*;
import ru.practicum.ewm.model.EventStateAdminAction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class EventModerationByAdminDto {

    @NotNull
    private EventStateAdminAction stateAction;

    @Size(min = 1, max = 1000)
    private String commentOnRejection;

}

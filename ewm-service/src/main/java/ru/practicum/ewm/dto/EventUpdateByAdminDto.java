package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.model.EventStateAdminAction;
import ru.practicum.ewm.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class EventUpdateByAdminDto {

    @Size(min = 3, max = 120)
    private String title;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @Size(min = 20, max = 2000)
    private String annotation;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private EventStateAdminAction stateAction;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime eventDate;

}

package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class EventNewDto {
    @NotBlank
    @Size(min = 3, max = 120)
    String title;

    @NotBlank
    @Size(min = 20, max = 7000)
    String description;

    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;

    @NotNull
    @Positive
    Long category;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    LocalDateTime eventDate;

    @NotNull
    Location location;

    Boolean paid = false;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;
}

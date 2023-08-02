package ru.practicum.ewm.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompilationDto {

    @NotNull
    private Long id;

    @NotNull
    private Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    private List<EventShortDto> events;
}

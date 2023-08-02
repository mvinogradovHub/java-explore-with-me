package ru.practicum.ewm.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompilationNewDto {

    private Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    private List<Long> events;
}

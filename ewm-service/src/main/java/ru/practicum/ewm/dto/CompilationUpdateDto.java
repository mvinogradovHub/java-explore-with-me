package ru.practicum.ewm.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompilationUpdateDto {

    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;

    private List<Long> events;
}

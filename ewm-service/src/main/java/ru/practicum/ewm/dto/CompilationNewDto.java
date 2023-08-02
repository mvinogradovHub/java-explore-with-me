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

    Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50)
    String title;

    List<Long> events;
}

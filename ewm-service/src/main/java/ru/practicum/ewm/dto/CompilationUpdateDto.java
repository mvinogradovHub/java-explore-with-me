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

    Boolean pinned;

    @Size(min = 1, max = 50)
    String title;

    List<Long> events;
}

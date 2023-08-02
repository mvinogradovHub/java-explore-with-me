package ru.practicum.ewm.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryDto {

    private Long id;

    @NotBlank
    private String name;
}

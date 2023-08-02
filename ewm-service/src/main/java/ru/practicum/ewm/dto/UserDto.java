package ru.practicum.ewm.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String email;
}

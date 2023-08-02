package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "locations")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
}

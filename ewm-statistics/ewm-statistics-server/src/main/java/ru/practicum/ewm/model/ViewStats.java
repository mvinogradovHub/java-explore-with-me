package ru.practicum.ewm.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewStats {
    String app;
    String uri;
    Long hits;
}

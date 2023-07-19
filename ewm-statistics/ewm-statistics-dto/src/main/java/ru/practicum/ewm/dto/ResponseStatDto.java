package ru.practicum.ewm.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStatDto {
    String app;
    String uri;
    Long hits;

    @Override
    public String toString() {
        return "ResponseStatDto{" +
                "app='" + app + '\'' +
                ", uri='" + uri + '\'' +
                ", hits=" + hits +
                '}';
    }
}

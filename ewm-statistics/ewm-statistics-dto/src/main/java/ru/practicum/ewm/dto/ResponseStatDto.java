package ru.practicum.ewm.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStatDto {
    private String app;
    private String uri;
    private Long hits;

    @Override
    public String toString() {
        return "ResponseStatDto{" +
                "app='" + app + '\'' +
                ", uri='" + uri + '\'' +
                ", hits=" + hits +
                '}';
    }
}

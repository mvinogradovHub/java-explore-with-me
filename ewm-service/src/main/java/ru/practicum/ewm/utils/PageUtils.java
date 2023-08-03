package ru.practicum.ewm.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtils {
    public static Pageable convertToPageSettings(Integer from, Integer size) {
        int page = from >= 0 ? Math.round((float) from / size) : -1;
        return PageRequest.of(page, size);
    }

    public static Pageable convertToPageSettings(Integer from, Integer size, String sortingByField) {
        int page = from >= 0 ? Math.round((float) from / size) : -1;
        return PageRequest.of(page, size, Sort.by(sortingByField).descending());
    }
}

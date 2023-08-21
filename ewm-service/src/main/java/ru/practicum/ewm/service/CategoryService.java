package ru.practicum.ewm.service;


import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(Category category);

    CategoryDto editCategory(Long catId, Category category);

    void deleteCategory(Long catId);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long catId);

}

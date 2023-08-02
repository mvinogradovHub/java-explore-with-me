package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.utils.PageUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(Category category) {
        if (categoryRepository.findByName(category.getName()).isEmpty()) {
            return categoryMapper.categoryToCategoryDto(categoryRepository.save(category));
        } else {
            throw new ConflictException("The name Category " + category.getName() + " already exist");
        }
    }

    @Override
    public CategoryDto editCategory(Long catId, Category category) {
        categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        category.setId(catId);
        Optional<Category> categoryOptional = categoryRepository.findByName(category.getName());
        if (categoryOptional.isEmpty() || (categoryOptional.get().equals(category))) {
            return categoryMapper.categoryToCategoryDto(categoryRepository.save(category));
        } else {
            throw new ConflictException("The Category with name" + category.getName() + " already exist");
        }

    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        List<Event> events = eventRepository.findByCategoryId(category.getId());
        if (!events.isEmpty()) {
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Page<Category> categories = categoryRepository.findAll(PageUtils.convertToPageSettings(from, size));
        return categories.stream().map(categoryMapper::categoryToCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        return categoryMapper.categoryToCategoryDto(category);
    }

}

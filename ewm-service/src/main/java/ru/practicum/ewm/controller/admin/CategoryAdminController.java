package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody Category category) {
        log.info("Received request to POST /admin/categories with body: {}", category);
        return categoryService.addCategory(category);
    }

    @PatchMapping("/{catId}")
    public CategoryDto editCategory(@Valid @RequestBody Category category, @PathVariable Long catId) {
        log.info("Received request to PATCH /admin/categories/{} with body: {}", catId, category);
        return categoryService.editCategory(catId, category);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Received request to DELETE /admin/categories/{}", catId);
        categoryService.deleteCategory(catId);
    }

}

package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.model.category.Category;

public class CategoryMapper {

    public static Category dtoToCategory(NewCategoryDto newCategoryDto) {
        return Category.builder().name(newCategoryDto.getName()).build();
    }

    public static CategoryDto categoryToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}

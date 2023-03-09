package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.repository.category.CategoryRepository;

import static ru.practicum.ewm.mapper.CategoryMapper.categoryToDto;
import static ru.practicum.ewm.mapper.CategoryMapper.dtoToCategory;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto adminCreateCategory(NewCategoryDto categoryDto) {
        Category category = dtoToCategory(categoryDto);
        return categoryToDto(categoryRepository.save(category));
    }

    @Transactional
    public void adminDeleteCategory(Long categoryId) {
        try {
            categoryRepository.deleteById(categoryId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Category " + categoryId + " not found");
        }
    }

    @Transactional
    public CategoryDto adminUpdateCategory(NewCategoryDto categoryDto, Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category " + categoryDto + " not found"));
        category.setName(categoryDto.getName());
        return categoryToDto(categoryRepository.save(category));
    }
}

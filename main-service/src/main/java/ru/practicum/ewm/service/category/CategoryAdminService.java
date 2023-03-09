package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.repository.category.CategoryRepository;
import ru.practicum.ewm.repository.event.EventRepository;

import javax.validation.ConstraintViolationException;

import static ru.practicum.ewm.mapper.CategoryMapper.categoryToDto;
import static ru.practicum.ewm.mapper.CategoryMapper.dtoToCategory;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CategoryDto adminCreateCategory(NewCategoryDto categoryDto) {
        Category category = dtoToCategory(categoryDto);
        return categoryToDto(categoryRepository.save(category));
    }

    @Transactional
    public ResponseEntity<Object> adminDeleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category " + categoryId + " not found"));
        eventRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new BadRequestException("There is events associated with this categoryId"));
        categoryRepository.deleteById(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Transactional
    public Category adminUpdateCategory(Category category, Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category " + category + " not found"));
        try {
            return categoryRepository.save(category);
        } catch (ConstraintViolationException e) {
            throw new BadRequestException("There is events associated with this category");
        }
    }
}

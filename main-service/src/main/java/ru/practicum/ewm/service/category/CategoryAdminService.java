package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.repository.category.CategoryRepository;
import ru.practicum.ewm.repository.event.EventRepository;

import javax.validation.ConstraintViolationException;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    public Category adminCreateCategory(Category category) {
        categoryRepository.findByName(category.getName()).orElseThrow(() -> new BadRequestException("Category with this name already exists"));
        return categoryRepository.save(category);
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

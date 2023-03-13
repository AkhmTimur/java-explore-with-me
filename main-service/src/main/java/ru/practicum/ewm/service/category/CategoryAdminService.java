package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.repository.category.CategoryRepository;
import ru.practicum.ewm.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

import static ru.practicum.ewm.mapper.CategoryMapper.categoryToDto;
import static ru.practicum.ewm.mapper.CategoryMapper.dtoToCategory;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CategoryDto adminCreateCategory(NewCategoryDto categoryDto) {
        categoryDto.setName(categoryDto.getName().trim());
        Optional<Category> categoryByName = categoryRepository.findByName(categoryDto.getName());
        if (categoryByName.isPresent()) {
            throw new DataConflictException("Category with this name already exists");
        }
        Category category = dtoToCategory(categoryDto);
        return categoryToDto(categoryRepository.save(category));
    }

    @Transactional
    public void adminDeleteCategory(Long categoryId) {
        List<Event> eventList = eventRepository.findAllByCategoryId(categoryId);
        if (!eventList.isEmpty()) {
            throw new DataConflictException("There is event associated with this category");
        }
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
        Optional<Category> categoryByName = categoryRepository.findByName(category.getName());
        if (categoryByName.isPresent() && !category.getId().equals(categoryByName.get().getId())) {
            throw new DataConflictException("Category with this name already exists");
        }
        category.setName(categoryDto.getName());
        return categoryToDto(categoryRepository.save(category));
    }
}

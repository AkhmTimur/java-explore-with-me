package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.repository.category.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.CategoryMapper.categoryToDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryPublicService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getCategories(Long from, Integer size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<Category> foundCategories = categoryRepository
                .findAllByIdIsGreaterThanEqualOrderByIdAsc(from, pageRequest);
        return foundCategories.stream().map(CategoryMapper::categoryToDto).collect(Collectors.toList());
    }

    public CategoryDto getCategory(Long catId) {
        return categoryToDto(getCategoryIfExist(catId));
    }

    public Category getCategoryIfExist(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(Category.class.getSimpleName() + " not found"));
    }
}

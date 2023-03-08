package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.repository.category.CategoryRepository;
import ru.practicum.ewm.validator.EntityValidator;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.CategoryMapper.categoryToDto;

@Service
@RequiredArgsConstructor
public class CategoryPublicService {
    private final CategoryRepository categoryRepository;
    private final EntityValidator entityValidator;

    @Transactional
    public List<CategoryDto> getCategories(Long from, Integer size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<Category> foundCategories = categoryRepository
                .findAllByIdIsGreaterThanEqualOrderByIdAsc(from, pageRequest);
        return foundCategories.stream().map(CategoryMapper::categoryToDto).collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto getCategory(Long catId) {
        return categoryToDto(entityValidator.getCategoryIfExist(catId));
    }
}

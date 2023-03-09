package ru.practicum.ewm.controller.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.service.category.CategoryAdminService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody @NotNull @Valid NewCategoryDto categoryDto) {
        return new ResponseEntity<>(categoryAdminService.adminCreateCategory(categoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long catId) {
        categoryAdminService.adminDeleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<Object> updateCategory(@RequestBody @NotNull @Valid NewCategoryDto categoryDto, @PathVariable Long catId) {
        return new ResponseEntity<>(categoryAdminService.adminUpdateCategory(categoryDto, catId), HttpStatus.OK);
    }
}

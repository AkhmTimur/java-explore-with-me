package ru.practicum.ewm.controller.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.service.category.CategoryAdminService;

@Controller
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody Category category) {
        return new ResponseEntity<>(categoryAdminService.adminCreateCategory(category), HttpStatus.OK);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long catId) {
        return categoryAdminService.adminDeleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<Object> updateCategory(@RequestBody Category category, @PathVariable Long catId) {
        return new ResponseEntity<>(categoryAdminService.adminUpdateCategory(category, catId), HttpStatus.OK);
    }
}

package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.category.CategoryPublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping
@Validated
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryPublicService categoryPublicService;

    @GetMapping
    public ResponseEntity<Object> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        return new ResponseEntity<>(categoryPublicService.getCategories(from, size), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<Object> getCategory(@PathVariable Long catId) {
        return new ResponseEntity<>(categoryPublicService.getCategory(catId), HttpStatus.OK);
    }
}

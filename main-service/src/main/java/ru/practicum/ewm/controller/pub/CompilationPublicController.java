package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.compilation.CompilationPublicService;

@RestController
@RequestMapping(path = "/compilations")
@Validated
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationPublicService compilationPublicService;

    @GetMapping("/{compId}")
    public ResponseEntity<Object> getCompilation(@PathVariable Long compId) {
        return new ResponseEntity<>(compilationPublicService.getCompilation(compId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getCompilations(@RequestParam(defaultValue = "0") Long from,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestParam(required = false) boolean pinned) {
        return new ResponseEntity<>(compilationPublicService.getCompilations(from, size, pinned), HttpStatus.OK);
    }
}

package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.service.compilation.CompilationAdminService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/admin/compilations")
@Validated
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationAdminService compilationAdminService;

    @PostMapping
    public ResponseEntity<Object> addCompilation(@RequestBody @NotNull @Valid NewCompilationDto newCompilationDto) {
        return new ResponseEntity<>(compilationAdminService.addCompilation(newCompilationDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{comId}")
    public ResponseEntity<Object> deleteCompilation(@PathVariable Long comId) {
        compilationAdminService.deleteCompilation(comId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{comId}")
    public ResponseEntity<Object> updateCompilation(@PathVariable Long comId, @RequestBody @Valid UpdateCompilationRequest request) {
        return new ResponseEntity<>(compilationAdminService.updateCompilation(comId, request), HttpStatus.OK);
    }
}


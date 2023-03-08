package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.service.user.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody NewUserRequest request) {
        return new ResponseEntity<>(userService.addUser(request), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers(@RequestParam(name = "ids", defaultValue = "") List<Long> ids,
                                           @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Long id,
                                           @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        if (ids.size() > 0) {
            return new ResponseEntity<>(userService.getAllUsers(id, size), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(userService.getUsers(ids), HttpStatus.OK);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

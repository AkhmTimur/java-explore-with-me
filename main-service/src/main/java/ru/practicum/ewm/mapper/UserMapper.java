package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.dto.user.UserDto;

public class UserMapper {

    public static User userRequestToUser(NewUserRequest userRequest) {
        return User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();
    }

    public static UserDto userToDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static UserShortDto userToShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}

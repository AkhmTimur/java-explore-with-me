package ru.practicum.ewm.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.UserMapper.userRequestToUser;
import static ru.practicum.ewm.mapper.UserMapper.userToDto;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto addUser(NewUserRequest userDto) {
        return userToDto(userRepository.save(userRequestToUser(userDto)));
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.deleteById(userId);
    }

    public List<UserDto> getUsers(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return users.stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }

    public List<UserDto> getAllUsers(long id, int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<User> users = userRepository.findAllByIdIsGreaterThanEqual(id, pageRequest);
        return users.stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }
}

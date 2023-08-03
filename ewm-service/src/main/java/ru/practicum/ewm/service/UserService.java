package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.model.User;

import java.util.List;

public interface UserService {

    UserDto addUser(User user);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer to);

    void deleteUser(Long userId);

}

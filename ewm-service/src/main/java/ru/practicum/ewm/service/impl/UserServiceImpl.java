package ru.practicum.ewm.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.QUser;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.utils.PageUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(User user) {
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        if (ids == null) {
            Page<User> users = userRepository.findAll(PageUtils.convertToPageSettings(from, size));
            return users.stream().map(userMapper::userToUserDto).collect(Collectors.toList());
        } else {
            BooleanExpression byUserInIds = QUser.user.id.in(ids);
            Page<User> users = userRepository.findAll(byUserInIds, PageUtils.convertToPageSettings(from, size));
            return users.stream().map(userMapper::userToUserDto).collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        userRepository.deleteById(userId);

    }
}

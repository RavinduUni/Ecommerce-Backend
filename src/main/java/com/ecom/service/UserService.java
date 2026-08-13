package com.ecom.service;

import com.ecom.dto.User;
import com.ecom.entity.UserEntity;
import com.ecom.mapper.UserMapper;
import com.ecom.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    public UserService(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
    }

    public List<User> getAllUsers(){
        return repository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    public User getUserById(Long id){
        Optional<UserEntity> existingUser = repository.findById(id);
        return existingUser.map(userMapper::toUserDto).orElseGet(() -> userMapper.toUserDto(existingUser.get()));
    }

    public User createUser(User user){
        UserEntity save = repository.save(userMapper.toUserEntity(user));
        return userMapper.toUserDto(save);
    }

    public Boolean updateUser(Long id, User updatedUser){
        return repository.findById(id)
                .map(existingUser -> {
                    userMapper.updateUserEntityFromDto(updatedUser, existingUser);
                    repository.save(existingUser);
                    return true;
                }).orElse(false);
    }
}

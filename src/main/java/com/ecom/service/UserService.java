package com.ecom.service;

import com.ecom.dto.User;
import com.ecom.entity.UserEntity;
import com.ecom.mapper.UserMapper;
import com.ecom.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder encoder;


    public UserService(UserRepository repository, UserMapper userMapper, AuthenticationManager authenticationManager, JWTService jwtService, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    public List<User> getAllUsers(){
        return repository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    public User getUserById(Long id){
        Optional<UserEntity> existingUser = repository.findById(id);
        return existingUser.map(userMapper::toUserDto).orElseGet(() -> userMapper.toUserDto(existingUser.get()));
    }

    public Map<String, Object> createUser(User user){

        user.setPassword(encoder.encode(user.getPassword()));

        UserEntity save = repository.save(userMapper.toUserEntity(user));

        User savedUserDto = userMapper.toUserDto(save);

        String token = jwtService.generateToken(save.getEmail());

        Map<String, Object> register = new HashMap<>();
        register.put("student", savedUserDto);
        register.put("token", token);

        return register;
    }

    public Boolean updateUser(Long id, User updatedUser){
        return repository.findById(id)
                .map(existingUser -> {
                    userMapper.updateUserEntityFromDto(updatedUser, existingUser);
                    repository.save(existingUser);
                    return true;
                }).orElse(false);
    }

    public String login(User user) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );

        if (authentication.isAuthenticated())
            return jwtService.generateToken(user.getEmail());
        else
            return "Login Failed";
    }
}

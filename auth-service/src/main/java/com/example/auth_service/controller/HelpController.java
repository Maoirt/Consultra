package com.example.auth_service.controller;

import com.example.auth_service.dto.UserDto;
import com.example.auth_service.mapper.UserMapper;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.dto.response.HelpUserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "HelpController", description = "Контроллер для справки и получения информации о пользователях")
public class HelpController {

    private static final Logger log = LoggerFactory.getLogger(HelpController.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/user/{id}")
    public HelpUserResponse getUserById(@PathVariable UUID id) {
        log.info("GET /user/{}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        UserDto userDto = userMapper.toUserDto(user);
        
        HelpUserResponse response = new HelpUserResponse();
        response.setId(userDto.getId());
        response.setEmail(userDto.getEmail());
        response.setUserName(userDto.getUserName());
        response.setFirstName(userDto.getFirstName());
        response.setLastName(userDto.getLastName());
        response.setPhone(userDto.getPhone());
        response.setRole(userDto.getRole());
        
        return response;
    }

}

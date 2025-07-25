package com.example.auth_service.mapper;

import com.example.auth_service.dto.SignUpDto;
import com.example.auth_service.dto.UserDto;
import com.example.auth_service.model.User;
import com.example.auth_service.model.User.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", expression = "java(userDto.getRole() != null ? UserRole.valueOf(userDto.getRole().toUpperCase()) : UserRole.USER)")
    User signUpToUser(SignUpDto userDto);
    User userDtoToUser(UserDto userDto);
}

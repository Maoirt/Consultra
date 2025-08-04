package com.example.auth_service.mapper;

import com.example.auth_service.dto.SignUpDto;
import com.example.auth_service.dto.UpdateProfileDto;
import com.example.auth_service.dto.UserDto;
import com.example.auth_service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "userName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UpdateProfileDto toUpdateProfileDto(User user);

}

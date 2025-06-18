package com.example.auth_service.service;

import com.example.auth_service.dto.*;

import java.util.Map;
import java.util.UUID;

/**
 * Сервис управления пользователями
 */
public interface UserService {

    /**
     * Поиск пользователя по email
     *
     * @param email email пользователя
     * @return пользователь
     */
    UserDto findByEmail(String email);

    /**
     * Авторизация пользователя
     *
     * @param credentialsDto данные пользователя
     * @return пользователь
     */
    UserDto login(CredentialsDto credentialsDto);

    /**
     * Регистрация пользователя
     *
     * @param userDto данные пользователя
     * @return пользователь
     */
    UserDto register(SignUpDto userDto);
    
    /**
     * Обновление профиля пользователя
     *
     * @param email email пользователя
     * @param userDto данные для обновления
     * @return обновленный профиль
     */
    UpdateProfileDto updateProfile(String email, UpdateProfileDto userDto);
    
    /**
     * Получение профиля пользователя
     *
     * @param email email пользователя
     * @return профиль пользователя
     */
    UpdateProfileDto getUserProfile(String email);
}
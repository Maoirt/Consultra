package com.example.auth_service.service.impl;

import com.example.auth_service.dto.UserDto;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.security.UserAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService extends OidcUserService {
//
//    private final UserRepository userRepository;
//    private final UserAuthProvider userAuthProvider;
//
//    @Override
//    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
//
//        OidcIdToken idToken = userRequest.getIdToken();
//        OidcUser oidcUser = super.loadUser(userRequest);
//
//        Map<String, Object> attributes = oidcUser.getAttributes();
//
//        String clientName = userRequest.getClientRegistration().getRegistrationId();
//
//        String email = null, firstname = null, lastname = null, username = null, authProvider = null, phone = null;
//
//        switch (clientName){
//            case "google":
//                username = (String) attributes.get("sub");
//                email = (String) attributes.get("email");
//                firstname = (String) attributes.get("given_name");
//                lastname = (String) attributes.get("family_name");
//                authProvider = "google";
//                break;
//            case "github":
//                username = (String) attributes.get("login");
//                email = (String) attributes.get("email");
//                firstname = (String) attributes.get("name");
//                authProvider = "github";
//                lastname = "";
//                break;
//            default:
//                throw new IllegalArgumentException("Неподдерживаемый провайдер: " + clientName);
//
//        }
//
//        Optional<User> existingUser = userRepository.findByUserName(username);
//
//        if(!existingUser.isPresent()){
//            User newUser = User.builder()
//                    .userName(username)
//                    .email(email)
//                    .firstName(firstname)
//                    .lastName(lastname)
//                    .authProvider(authProvider)
//                    .enable(true)
//                    .phone(phone)
//                    .build();
//
//            userRepository.save(newUser);
//        }
//
//        String token = userAuthProvider.createToken(username);
//
//        Map<String, Object> updatedAttributes = new HashMap<>(attributes);
//        updatedAttributes.put("auth_token", token);
//
//        OidcUserInfo oidcUserInfo = new OidcUserInfo(updatedAttributes);
//
//        Set<GrantedAuthority> authorities = Collections.singleton(new OAuth2UserAuthority(attributes));
//        String userNameAttributesKey = userRequest.getClientRegistration()
//                .getProviderDetails()
//                .getUserInfoEndpoint()
//                .getUserNameAttributeName();
//
//        OidcUser updateUser = new DefaultOidcUser(authorities, idToken, oidcUserInfo, userNameAttributesKey);
//
//        return updateUser;
//    }
//
//}

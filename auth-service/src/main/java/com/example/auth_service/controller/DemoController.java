package com.example.auth_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.auth_service.dto.response.DemoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@Tag(name = "DemoController", description = "Контроллер для демонстрации")
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/messages")
    public DemoResponse messages(){
        log.info("GET /messages");
        List<String> messages = Arrays.asList("hello", "world");
        return new DemoResponse(messages, messages.size());
    }
}

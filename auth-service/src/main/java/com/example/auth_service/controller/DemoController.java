package com.example.auth_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<String>> messages(){
        log.info("GET /messages");
        return ResponseEntity.ok(Arrays.asList("hello", "world"));
    }
}

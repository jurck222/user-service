package org.task.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.task.userservice.enums.Role;
import org.task.userservice.exceptions.EmailAlreadyInUseException;
import org.task.userservice.response.AuthenticationResponse;
import org.task.userservice.request.RegisterRequest;
import org.task.userservice.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) throws EmailAlreadyInUseException {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}

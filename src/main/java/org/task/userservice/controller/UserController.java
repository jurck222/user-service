package org.task.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.task.userservice.entity.User;
import org.task.userservice.entity.UserNameSurname;
import org.task.userservice.enums.MedicalService;
import org.task.userservice.enums.Role;
import org.task.userservice.response.InfoResponse;
import org.task.userservice.service.AuthenticationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService service;

    @GetMapping("/")
    public ResponseEntity<InfoResponse> getUser(@RequestHeader(name = "Authorization") String token){
        return ResponseEntity.ok(service.getUserInfo(token.substring(7)));
    }

    @GetMapping("/role")
    public ResponseEntity<Role> getUserRole(@RequestHeader(name = "Authorization") String token){
        return ResponseEntity.ok(service.getUserRole(token.substring(7)));
    }

    @GetMapping("/doctors/")
    public List<UserNameSurname> getDoctorsForService(@RequestParam MedicalService medicalService){
        return service.getDoctorsForService(medicalService);
    }

    @GetMapping("/userId")
    public int getUserId(@RequestHeader(name = "Authorization") String token){
        return service.getUserId(token.substring(7));
    }

    @GetMapping("/userInfo/{id}")
    public ResponseEntity<InfoResponse> getUserInfo(@PathVariable int id){
        return ResponseEntity.ok(service.getUserInfoById(id));
    };
}

package org.task.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.task.userservice.entity.UserNameSurname;
import org.task.userservice.enums.MedicalService;
import org.task.userservice.response.InfoResponse;
import org.task.userservice.service.AuthenticationService;

import java.util.Arrays;
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

    @GetMapping("/doctors/")
    public List<UserNameSurname> getDoctorsForService(@RequestParam String medicalService){
        return service.getDoctorsForService(medicalService);
    }

    @GetMapping("/userId")
    public int getUserId(@RequestHeader(name = "Authorization") String token){
        return service.getUserId(token.substring(7));
    }

    @GetMapping("/userInfo/{id}")
    public ResponseEntity<InfoResponse> getUserInfo(@PathVariable int id){
        return ResponseEntity.ok(service.getUserInfoById(id));
    }

    @GetMapping("/services")
    public List<String> getServices(){
        return Arrays.stream(MedicalService.values())
                .map(MedicalService::getValue)
                .toList();
    }
}

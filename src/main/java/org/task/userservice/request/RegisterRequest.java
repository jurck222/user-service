package org.task.userservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.task.userservice.enums.MedicalService;
import org.task.userservice.enums.Role;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private List<MedicalService> services;
}


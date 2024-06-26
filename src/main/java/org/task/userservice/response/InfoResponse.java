package org.task.userservice.response;

import jakarta.persistence.ElementCollection;
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
public class InfoResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role role;
    private List<MedicalService> services;
}

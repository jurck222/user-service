package org.task.userservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.InvalidClaimException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.task.userservice.entity.UserNameSurname;
import org.task.userservice.enums.MedicalService;
import org.task.userservice.enums.Role;
import org.task.userservice.exceptions.EmailAlreadyInUseException;
import org.task.userservice.response.AuthenticationResponse;
import org.task.userservice.request.RegisterRequest;
import org.task.userservice.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.task.userservice.repository.UserRepository;
import org.task.userservice.response.InfoResponse;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) throws EmailAlreadyInUseException {
        if(userRepository.findByEmail(request.getEmail()).isEmpty()) {
            var user = User.builder()
                    .firstName(request.getFirstname())
                    .lastName(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .phone(request.getPhone())
                    .services(request.getServices())
                    .build();

            userRepository.save(user);
            var role = user.getRole();
            HashMap<String, Object> extraclaims = new HashMap<>();
            extraclaims.put("role", role);
            var jwtToken = jwtService.generateToken(extraclaims, user);
            return AuthenticationResponse.builder().token(jwtToken).role(role).build();
        }

        throw new EmailAlreadyInUseException("User with this email already exists");
    }

    public AuthenticationResponse authenticate(RegisterRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
        ));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var role = user.getRole();
        HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);
        var jwtToken = jwtService.generateToken(extraClaims, user);
        return AuthenticationResponse.builder().token(jwtToken).role(role).build();
    }

    public InfoResponse getUserInfo(String token){
        var user = userRepository.findByEmail(jwtService.extractUsername(token));
        if(user.isPresent()){
            return InfoResponse.builder()
                    .id(user.get().getId())
                    .phone(user.get().getPhone())
                    .services(user.get().getServices())
                    .email(user.get().getEmail())
                    .firstName(user.get().getFirstName())
                    .lastName(user.get().getLastName())
                    .role(user.get().getRole())
                    .build();
        }
        throw new UsernameNotFoundException("User not found");
    }

    public Role getUserRole(String token) {
        var user = userRepository.findByEmail(jwtService.extractUsername(token));
        if(user.isPresent()){
            return user.get().getRole();
        }
        throw new UsernameNotFoundException("User not found");
    }

    public Boolean validate(String token, Role role) {
        var user = userRepository.findByEmail(jwtService.extractUsername(token));
        if(user.isPresent()){
            return user.get().getRole() == role;
        }
        throw new InvalidParameterException("Wrong role");
    }

    public List<UserNameSurname> getDoctorsForService(MedicalService medicalService) {
        var users = userRepository.findUsersByService(medicalService);
        if(users.isPresent()){
            return users.get();
        }
        throw new InvalidParameterException("No doctors for this service");
    }

    public int getUserId(String token) {
        var user = userRepository.findByEmail(jwtService.extractUsername(token));
        if(user.isPresent()){
            return user.get().getId();
        }
        throw new UsernameNotFoundException("User not found");
    }

    public InfoResponse getUserInfoById(int id){
        var user = userRepository.findById(id);
        if(user.isPresent()){
            if(user.get().getRole().equals(Role.PATIENT)) {
                return InfoResponse.builder()
                        .id(user.get().getId())
                        .phone(user.get().getPhone())
                        .email(user.get().getEmail())
                        .firstName(user.get().getFirstName())
                        .lastName(user.get().getLastName())
                        .role(user.get().getRole())
                        .build();
            }
            return InfoResponse.builder()
                    .id(user.get().getId())
                    .phone(user.get().getPhone())
                    .email(user.get().getEmail())
                    .firstName(user.get().getFirstName())
                    .lastName(user.get().getLastName())
                    .services(user.get().getServices())
                    .role(user.get().getRole())
                    .build();
        }
        throw new UsernameNotFoundException("User not found");
    }
}

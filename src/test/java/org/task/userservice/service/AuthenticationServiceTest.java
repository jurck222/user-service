package org.task.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.task.userservice.entity.User;
import org.task.userservice.entity.UserNameSurname;
import org.task.userservice.enums.MedicalService;
import org.task.userservice.enums.Role;
import org.task.userservice.exceptions.EmailAlreadyInUseException;
import org.task.userservice.repository.UserRepository;
import org.task.userservice.request.RegisterRequest;
import org.task.userservice.response.AuthenticationResponse;
import org.task.userservice.response.InfoResponse;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(userRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    public void testRegisterSuccess() throws EmailAlreadyInUseException {
        RegisterRequest request = RegisterRequest.builder()
                .email("testUser@mail.com")
                .password("doktor")
                .firstname("Janez")
                .lastname("Kranjski")
                .phone("555-666-777")
                .role(Role.DOCTOR)
                .services(List.of(MedicalService.GENERAL_CHECKUP, MedicalService.DENTAL_CLEANING))
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedDoktor");

        User user = User.builder()
                .firstName("Janez")
                .lastName("Kranjski")
                .email("testUser@mail.com")
                .password("encodedDoktor")
                .role(Role.DOCTOR)
                .phone("555-666-777")
                .services(List.of(MedicalService.GENERAL_CHECKUP, MedicalService.DENTAL_CLEANING))
                .build();

        String token = "token";
        HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", Role.DOCTOR);

        when(jwtService.generateToken(extraClaims, user)).thenReturn(token);

        AuthenticationResponse response = authenticationService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals("Janez", capturedUser.getFirstName());
        assertEquals("Kranjski", capturedUser.getLastName());
        assertEquals("testUser@mail.com", capturedUser.getEmail());
        assertEquals(Role.DOCTOR, capturedUser.getRole());
        assertEquals("555-666-777", capturedUser.getPhone());
        assertEquals(List.of(MedicalService.GENERAL_CHECKUP, MedicalService.DENTAL_CLEANING), capturedUser.getServices());

        assertEquals(token, response.getToken());
        assertEquals(Role.DOCTOR, response.getRole());
    }

    @Test
    public void testRegisterEmailAlreadyInUse() {
        RegisterRequest request = RegisterRequest.builder()
                .email("testUser@mail.com")
                .password("doktor")
                .firstname("Janez")
                .lastname("Kranjski")
                .phone("555-666-777")
                .role(Role.DOCTOR)
                .services(List.of(MedicalService.GENERAL_CHECKUP, MedicalService.DENTAL_CLEANING))
                .build();
        
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyInUseException.class, () -> authenticationService.register(request));
    }

    @Test
    public void testAuthenticateSuccess() {
        RegisterRequest request = RegisterRequest
                .builder()
                .email("testUser@email.com")
                .password("pacient")
                .build();

        HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", Role.DOCTOR);

        User user = User.builder()
                .email("testUser@email.com")
                .role(Role.DOCTOR)
                .build();
        
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        String token = "token";
        when(jwtService.generateToken(extraClaims, user)).thenReturn(token);

        AuthenticationResponse response = authenticationService.authenticate(request);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        assertEquals(token, response.getToken());
    }

    @Test
    public void testGetUserInfoSuccess() {
        String token = "token";
        User user = User.builder()
                .id(1)
                .firstName("Janez")
                .lastName("Kranjski")
                .email("testUser@mail.com")
                .phone("1234567890")
                .role(Role.DOCTOR)
                .build();
        
        when(jwtService.extractUsername(token)).thenReturn("testUser@mail.com");
        when(userRepository.findByEmail("testUser@mail.com")).thenReturn(Optional.of(user));

        InfoResponse response = authenticationService.getUserInfo(token);

        assertEquals("Janez", response.getFirstName());
    }

    @Test
    public void testGetUserInfoNotFound() {
        String token = "token";
        when(jwtService.extractUsername(token)).thenReturn("testUser@mail.com");
        when(userRepository.findByEmail("testUser@mail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.getUserInfo(token));
    }

    @Test
    public void testGetDoctorsForServiceSuccess() {
        MedicalService service = MedicalService.fromString("General checkup");
        List<UserNameSurname> doctors = List.of(new UserNameSurname(1, "Janez", "Kranjski"));
        when(userRepository.findUsersByService(service)).thenReturn(Optional.of(doctors));

        List<UserNameSurname> result = authenticationService.getDoctorsForService("General checkup");

        assertEquals(1, result.size());
        assertEquals("Janez", result.getFirst().firstname());
    }

    @Test
    public void testGetDoctorsForServiceNoDoctors() {
        MedicalService service = MedicalService.fromString("General checkup");
        when(userRepository.findUsersByService(service)).thenReturn(Optional.empty());

        assertThrows(InvalidParameterException.class, () -> authenticationService.getDoctorsForService("General checkup"));
    }

    @Test
    public void testGetUserIdSuccess() {
        String token = "token";
        User user = User.builder()
                .id(1)
                .email("testUser@mail.com")
                .build();

        when(jwtService.extractUsername(token)).thenReturn("testUser@mail.com");
        when(userRepository.findByEmail("testUser@mail.com")).thenReturn(Optional.of(user));

        int userId = authenticationService.getUserId(token);

        assertEquals(1, userId);
    }

    @Test
    public void testGetUserIdNotFound() {
        String token = "token";
        when(jwtService.extractUsername(token)).thenReturn("testUser@mail.com");
        when(userRepository.findByEmail("testUser@mail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.getUserId(token));
    }

    @Test
    public void testGetUserInfoByIdWithPatientRoleSuccess() {
        User user = User.builder()
                .id(1)
                .firstName("Janez")
                .lastName("Kranjski")
                .email("testUser@mail.com")
                .phone("1234567890")
                .role(Role.PATIENT).build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        InfoResponse response = authenticationService.getUserInfoById(1);

        assertEquals("Janez", response.getFirstName());
    }

    @Test
    public void testGetUserInfoByIdWithDoctorRoleSuccess() {
        User user = User.builder()
                .id(1)
                .firstName("Janez")
                .lastName("Kranjski")
                .email("testUser@mail.com")
                .phone("1234567890")
                .role(Role.DOCTOR).build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        InfoResponse response = authenticationService.getUserInfoById(1);

        assertEquals("Janez", response.getFirstName());
    }

    @Test
    public void testGetUserInfoByIdNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.getUserInfoById(1));
    }
}
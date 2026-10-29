package com.smartstudy.service;

import com.smartstudy.dto.auth.AuthResponse;
import com.smartstudy.dto.auth.LoginRequest;
import com.smartstudy.dto.auth.RegisterRequest;
import com.smartstudy.entity.User;
import com.smartstudy.entity.UserPrivacySettings;
import com.smartstudy.entity.enums.Role;
import com.smartstudy.exception.BadRequestException;
import com.smartstudy.repository.UserPrivacySettingsRepository;
import com.smartstudy.repository.UserRepository;
import com.smartstudy.security.JwtTokenProvider;
import com.smartstudy.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPrivacySettingsRepository privacySettingsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                privacySettingsRepository,
                passwordEncoder,
                authenticationManager,
                tokenProvider
        );
    }

    @Test
    @DisplayName("Register should successfully create user with hashed password and return token")
    void register_Success() {
        RegisterRequest req = new RegisterRequest("Alice Student", "alice@example.com", "Password123!", Role.ROLE_STUDENT);

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("hashed_Password123!");

        User savedUser = new User("alice@example.com", "hashed_Password123!", "Alice Student", Role.ROLE_STUDENT);
        savedUser.setId(10L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("dummy_jwt_token");

        AuthResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("dummy_jwt_token", res.getAccessToken());
        assertEquals("alice@example.com", res.getEmail());
        assertEquals("Alice Student", res.getFullName());
        assertEquals(10L, res.getUserId());

        verify(passwordEncoder).encode("Password123!");
        verify(privacySettingsRepository).save(any(UserPrivacySettings.class));
    }

    @Test
    @DisplayName("Register should reject duplicate email addresses")
    void register_DuplicateEmail_ThrowsBadRequest() {
        RegisterRequest req = new RegisterRequest("Bob", "bob@example.com", "Password123!", Role.ROLE_STUDENT);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(req));
    }

    @Test
    @DisplayName("Login should return JWT when credentials are valid")
    void login_Success() {
        LoginRequest req = new LoginRequest("alice@example.com", "Password123!");

        User user = new User("alice@example.com", "hashed_pwd", "Alice Student", Role.ROLE_STUDENT);
        user.setId(10L);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("valid_jwt");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        AuthResponse res = authService.login(req);

        assertNotNull(res);
        assertEquals("valid_jwt", res.getAccessToken());
        assertEquals("alice@example.com", res.getEmail());
    }
}

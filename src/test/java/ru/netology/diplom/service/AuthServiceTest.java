package ru.netology.diplom.service;
import ru.netology.diplom.model.User;
import ru.netology.diplom.repository.AuthTokenRepository;
import ru.netology.diplom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthTokenRepository authTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "encodedPassword");
        testUser.setId(1L);
    }

    @Test
    void authenticate_Success() {
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(authTokenRepository.save(any())).thenReturn(null);

        Optional<String> result = authService.authenticate("testuser", "password");

        assertTrue(result.isPresent());
        verify(authTokenRepository).save(any());
    }

    @Test
    void authenticate_WrongPassword() {
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        Optional<String> result = authService.authenticate("testuser", "wrongpassword");

        assertFalse(result.isPresent());
        verify(authTokenRepository, never()).save(any());
    }
}
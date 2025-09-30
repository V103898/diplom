package ru.netology.diplom.service;
import ru.netology.diplom.model.AuthToken;
import ru.netology.diplom.model.User;
import ru.netology.diplom.repository.AuthTokenRepository;
import ru.netology.diplom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.jwt.expiration}")
    private long expiration;

    public Optional<String> authenticate(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = generateToken();
                LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

                AuthToken authToken = new AuthToken(token, user, expiresAt);
                authTokenRepository.save(authToken);

                return Optional.of(token);
            }
        }
        return Optional.empty();
    }

    public Optional<User> validateToken(String token) {
        Optional<AuthToken> authTokenOpt = authTokenRepository.findByToken(token);
        if (authTokenOpt.isPresent()) {
            AuthToken authToken = authTokenOpt.get();
            if (authToken.getExpiresAt().isAfter(LocalDateTime.now())) {
                return Optional.of(authToken.getUser());
            } else {
                authTokenRepository.delete(authToken);
            }
        }
        return Optional.empty();
    }

    public void logout(String token) {
        authTokenRepository.deleteByToken(token);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}

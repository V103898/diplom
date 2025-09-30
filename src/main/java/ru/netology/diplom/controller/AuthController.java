package ru.netology.diplom.controller;
import ru.netology.diplom.dto.ErrorResponse;
import ru.netology.diplom.dto.LoginRequest;
import ru.netology.diplom.dto.LoginResponse;
import ru.netology.diplom.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Optional;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<String> tokenOpt = authService.authenticate(
                loginRequest.getLogin(),
                loginRequest.getPassword()
        );

        if (tokenOpt.isPresent()) {
            return ResponseEntity.ok(new LoginResponse(tokenOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Bad credentials", 400));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String authToken) {
        authService.logout(authToken);
        return ResponseEntity.ok().build();
    }
}

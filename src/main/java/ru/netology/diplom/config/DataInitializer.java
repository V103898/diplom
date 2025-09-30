package ru.netology.diplom.config;
import ru.netology.diplom.model.User;
import ru.netology.diplom.repository.UserRepository;
import ru.netology.diplom.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public void run(String... args) throws Exception {
        // Initialize file storage
        fileStorageService.init();

        // Create test users
        if (!userRepository.existsByLogin("user1")) {
            User user1 = new User("user1", passwordEncoder.encode("password1"));
            userRepository.save(user1);
        }

        if (!userRepository.existsByLogin("user2")) {
            User user2 = new User("user2", passwordEncoder.encode("password2"));
            userRepository.save(user2);
        }
    }
}
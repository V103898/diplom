package ru.netology.diplom.repository;
import ru.netology.diplom.model.AuthToken;
import ru.netology.diplom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByUser(User user);
}
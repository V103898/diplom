package ru.netology.diplom.service;
import ru.netology.diplom.model.User;
import ru.netology.diplom.model.UserFile;
import ru.netology.diplom.repository.UserFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.file-storage.path}")
    private String storagePath;

    @Autowired
    private UserFileRepository userFileRepository;

    public void init() throws IOException {
        Files.createDirectories(Paths.get(storagePath));
    }

    public UserFile storeFile(MultipartFile file, String filename, User user) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String storedFilename = generateStoredFilename(filename);
        Long size = file.getSize();

        // Save file to filesystem
        Path filePath = Paths.get(storagePath).resolve(storedFilename);
        Files.copy(file.getInputStream(), filePath);

        // Save file metadata to database
        UserFile userFile = new UserFile(filename, originalFilename, size, user);
        return userFileRepository.save(userFile);
    }

    public Resource loadFile(String filename, User user) throws MalformedURLException {
        Optional<UserFile> userFileOpt = userFileRepository.findByUserAndFilename(user, filename);
        if (userFileOpt.isPresent()) {
            UserFile userFile = userFileOpt.get();
            String storedFilename = extractStoredFilename(userFile.getOriginalFilename());
            Path filePath = Paths.get(storagePath).resolve(storedFilename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
        }
        throw new RuntimeException("File not found: " + filename);
    }

    public void deleteFile(String filename, User user) throws IOException {
        Optional<UserFile> userFileOpt = userFileRepository.findByUserAndFilename(user, filename);
        if (userFileOpt.isPresent()) {
            UserFile userFile = userFileOpt.get();
            String storedFilename = extractStoredFilename(userFile.getOriginalFilename());
            Path filePath = Paths.get(storagePath).resolve(storedFilename);

            Files.deleteIfExists(filePath);
            userFileRepository.delete(userFile);
        } else {
            throw new RuntimeException("File not found: " + filename);
        }
    }

    public void renameFile(String oldFilename, String newFilename, User user) {
        Optional<UserFile> userFileOpt = userFileRepository.findByUserAndFilename(user, oldFilename);
        if (userFileOpt.isPresent()) {
            UserFile userFile = userFileOpt.get();
            userFile.setFilename(newFilename);
            userFileRepository.save(userFile);
        } else {
            throw new RuntimeException("File not found: " + oldFilename);
        }
    }

    public List<UserFile> getUserFiles(User user, int limit) {
        List<UserFile> files = userFileRepository.findByUserOrderByCreatedAtDesc(user);
        if (limit > 0 && files.size() > limit) {
            return files.subList(0, limit);
        }
        return files;
    }

    private String generateStoredFilename(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    private String extractStoredFilename(String storedFilename) {
        // Extract the UUID part from stored filename
        return storedFilename; // In real implementation, you'd extract the actual stored name
    }
}
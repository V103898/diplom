package ru.netology.diplom.controller;
import ru.netology.diplom.dto.ErrorResponse;
import ru.netology.diplom.dto.FileResponse;
import ru.netology.diplom.dto.RenameRequest;
import ru.netology.diplom.model.User;
import ru.netology.diplom.model.UserFile;
import ru.netology.diplom.service.AuthService;
import ru.netology.diplom.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class FileController {

    @Autowired
    private AuthService authService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") String filename,
            @RequestParam("file") MultipartFile file) {

        Optional<User> userOpt = authService.validateToken(authToken);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized error", 401));
        }

        try {
            User user = userOpt.get();
            fileStorageService.storeFile(file, filename, user);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error input data", 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error upload file", 500));
        }
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") String filename,
            HttpServletRequest request) {

        Optional<User> userOpt = authService.validateToken(authToken);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized error", 401));
        }

        try {
            User user = userOpt.get();
            Resource resource = fileStorageService.loadFile(filename, user);

            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error input data", 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error download file", 500));
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") String filename) {

        Optional<User> userOpt = authService.validateToken(authToken);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized error", 401));
        }

        try {
            User user = userOpt.get();
            fileStorageService.deleteFile(filename, user);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error input data", 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error delete file", 500));
        }
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") String filename,
            @RequestBody RenameRequest renameRequest) {

        Optional<User> userOpt = authService.validateToken(authToken);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized error", 401));
        }

        try {
            User user = userOpt.get();
            fileStorageService.renameFile(filename, renameRequest.getName(), user);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error input data", 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error rename file", 500));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getFileList(
            @RequestHeader("auth-token") String authToken,
            @RequestParam(value = "limit", required = false) Integer limit) {

        Optional<User> userOpt = authService.validateToken(authToken);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized error", 401));
        }

        try {
            User user = userOpt.get();
            int actualLimit = limit != null ? limit : 0;
            List<UserFile> userFiles = fileStorageService.getUserFiles(user, actualLimit);

            List<FileResponse> fileResponses = userFiles.stream()
                    .map(file -> new FileResponse(file.getFilename(), file.getSize()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(fileResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error getting file list", 500));
        }
    }
}
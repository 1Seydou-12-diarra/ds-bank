package com.said.dsbank.auth_users.service.impl;

import com.said.dsbank.auth_users.dtos.UpdatePasswordRequest;
import com.said.dsbank.auth_users.dtos.UserDTO;
import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.auth_users.repo.UserRepo;
import com.said.dsbank.auth_users.service.UserService;
import com.said.dsbank.exceptions.BadRequestException;
import com.said.dsbank.exceptions.NotFoundException;
import com.said.dsbank.notification.dtos.NotificationDTO;
import com.said.dsbank.notification.service.NotificationService;
import com.said.dsbank.res.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private final String upload = "uploads/profile-pictures/";

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new NotFoundException("User is not authenticated");
        }

        return userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public Response<UserDTO> getProfile() {
        User user = getCurrentUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User profile retrieved")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<Page<UserDTO>> getAllUsers(int page, int size) {
        Page<User> users = userRepo.findAll(PageRequest.of(page, size));

        Page<UserDTO> userDTOPage =
                users.map(user -> modelMapper.map(user, UserDTO.class));

        return Response.<Page<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .data(userDTOPage)
                .build();
    }

    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {

        User user = getCurrentUser();

        String oldPasswordInput = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();

        if (oldPasswordInput == null || newPassword == null) {
            throw new BadRequestException("Old or new password cannot be null");
        }

        // Vérification correcte : matches(rawPassword, encodedPassword)
        if (!passwordEncoder.matches(oldPasswordInput, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateAt(LocalDateTime.now());
        userRepo.save(user);

        // Send notification
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Your password has been changed")
                .templateName("password-changed") // doit correspondre au template réel
                .templateVariables(templateVariables)
                .build();

        notificationService.sendEmail(notificationDTO, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password changed successfully")
                .build();
    }

    @Override
    public Response<?> uploadProfilPicture(MultipartFile file) {

        User user = getCurrentUser();

        try {

            // Create folder if not exists
            Path uploadPath = Paths.get(upload);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Delete old picture if exists
            if (user.getProfilePictureUrl() != null) {
                Path oldFile = Paths.get(user.getProfilePictureUrl());
                if (Files.exists(oldFile)) {
                    Files.delete(oldFile);
                }
            }

            // File extension
            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null || !originalFileName.contains(".")) {
                throw new BadRequestException("Invalid file format");
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);

            // Unique file name
            String newFileName = UUID.randomUUID() + "." + extension;

            Path filePath = uploadPath.resolve(newFileName);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            String fileUrl = upload + newFileName;

            user.setProfilePictureUrl(fileUrl);
            userRepo.save(user);

            return Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .data(upload + newFileName)
                    .message("Profile picture uploaded successfully")
                    .build();

        } catch (Exception e) {
            log.error("File upload error: {}", e.getMessage());
            throw new BadRequestException("Failed to upload file");
        }
    }
}

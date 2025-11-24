package com.said.dsbank.auth_users.service;

import com.said.dsbank.auth_users.dtos.UpdatePasswordRequest;
import com.said.dsbank.auth_users.dtos.UserDTO;
import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.res.Response;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    User getCurrentUser();

    Response<UserDTO>getProfile();

    Response<Page<UserDTO>> getAllUsers(int page, int size);

    Response<?>updatePassword(UpdatePasswordRequest updatePasswordRequest);


    Response<?> uploadProfilPicture(MultipartFile file);
}

package com.said.dsbank.auth_users.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.said.dsbank.acount.dtos.AccountDTO;
import com.said.dsbank.role.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class UserDTO {
    private Long id;
    private String lastName;
    private String firstName;
    private String email;
    private String password;
    private String profilePicture;
    private List<Role> roles;

    @JsonManagedReference
    private List<AccountDTO> accounts;
    private LocalDateTime createdAt;
    private  LocalDateTime updateAt;



}

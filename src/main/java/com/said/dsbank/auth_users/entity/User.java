package com.said.dsbank.auth_users.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.said.dsbank.acount.entity.Account;
import com.said.dsbank.role.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  firstName;
    private String  lastName;

    @Email
    @Column(unique = true,nullable = false)
    @NotBlank(message= "Email is required")
    private String  email;
    private String  password;
    private String  profilePictureUrl;
    private boolean active = true;
    private String phoneNumber;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="users_roles",
            joinColumns =@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="role_id")
            )
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Account> accounts = new ArrayList<>();


    private LocalDateTime creatAt = LocalDateTime.now();
    private LocalDateTime updateAt = LocalDateTime.now();

}

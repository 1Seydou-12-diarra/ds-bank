package com.said.dsbank.auth_users.entity;

import com.said.dsbank.acount.entity.Account;
import com.said.dsbank.role.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity

@Table(name ="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String  lastName;
    private String  firstName;

    @Email
    @Column(unique = true,nullable = false)
    @NotBlank(message= "Email is required")
    private String  email;
    private String  password;
    private String  profilePictureUrl;
    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="users_roles",
            joinColumns =@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="role_id")
            )
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();


    private LocalDateTime creatAt = LocalDateTime.now();
    private LocalDateTime updateAt = LocalDateTime.now();

}

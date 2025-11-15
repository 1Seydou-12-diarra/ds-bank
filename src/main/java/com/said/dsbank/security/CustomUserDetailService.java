package com.said.dsbank.security;

import com.said.dsbank.auth_users.entity.User;
import com.said.dsbank.auth_users.repo.UserRepo;
import com.said.dsbank.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService  implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
         User user = userRepo.findByEmail(username)
                 .orElseThrow(()-> new NotFoundException("Email not found"));

         return AuthUser.builder()
                 .user(user)
                 .build();



    }
}

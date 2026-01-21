package com.example.jobnest.config;

import com.example.jobnest.entity.Users;
import com.example.jobnest.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("İstifadəçi tapılmadı: " + email));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("İstifadəçi hesabı aktiv deyil: " + email);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        String userType = user.getUserTypeId().getUserTypeName();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + userType.toUpperCase(java.util.Locale.ENGLISH)));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }
}

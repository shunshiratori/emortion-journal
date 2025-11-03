package com.example.emortion_journal.security;

import com.example.emortion_journal.model.UserEntity;
import com.example.emortion_journal.service.UserEntryService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserEntryService userEntryService;

    public CustomUserDetailsService(UserEntryService userEntryService) {
        this.userEntryService = userEntryService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity u = userEntryService.findByUsername(username);
        if (u == null) throw new UsernameNotFoundException("User not found: " + username);

        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities("USER")
                .build();
    }
}

package com.example.emortion_journal.service;

import com.example.emortion_journal.model.UserEntity;
import com.example.emortion_journal.model.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserEntryService {
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntryService(UserEntityRepository userEntityRepository, PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity registerNewUser(UserEntity user) {
        //ユーザー名が既に存在する確認
        if(userEntityRepository.findByUsername(user.getUsername()).isPresent()){
            throw new RuntimeException("username" + user.getUsername() + "already exists");
        }

        //パスワードをハッシュ化
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        //ユーザーをDBに登録
        return userEntityRepository.save(user);
    }

    public UserEntity findByUsername(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found" + username));
    }
}

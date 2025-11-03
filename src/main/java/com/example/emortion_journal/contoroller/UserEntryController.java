package com.example.emortion_journal.contoroller;

import com.example.emortion_journal.model.UserEntity;
import com.example.emortion_journal.security.JwtTokenUtil;
import com.example.emortion_journal.service.UserEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserEntryController {
    private final UserEntryService userEntryService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public UserEntryController(UserEntryService userEntryService, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.userEntryService = userEntryService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/users")
    public ResponseEntity<?> registerUser(@RequestBody UserEntity userEntity) {
        try {
            //パスワードのハッシュ化
            UserEntity registerUser = userEntryService.registerNewUser(userEntity);

            //応答?
            registerUser.setPassword(null);

            //201を返す
            return new ResponseEntity<>(registerUser, HttpStatus.CREATED);

        }catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            String token = jwtTokenUtil.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token, "username", username));

        } catch (org.springframework.security.authentication.BadCredentialsException |
                 org.springframework.security.authentication.InternalAuthenticationServiceException e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid username or password"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Login failed"));
        }
    }
}

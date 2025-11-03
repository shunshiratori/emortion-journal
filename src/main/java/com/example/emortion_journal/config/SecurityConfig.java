package com.example.emortion_journal.config;

import com.example.emortion_journal.model.UserEntity;
import com.example.emortion_journal.security.JwtRequestFilter;
import com.example.emortion_journal.security.JwtTokenUtil;
import com.example.emortion_journal.service.UserEntryService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // JwtRequestFilter は @Component か、@Bean でどちらか一方のみ
    @Bean
    public JwtRequestFilter jwtRequestFilter(UserEntryService userEntryService, JwtTokenUtil jwtTokenUtil) {
        return new JwtRequestFilter(userEntryService, jwtTokenUtil);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ★ ここで UserDetailsService を内蔵定義（CustomUserDetailsService クラス不要）
    @Bean
    public UserDetailsService userDetailsService(UserEntryService userEntryService) {
        return username -> {
            try {
                var u = userEntryService.findByUsername(username); // 見つからないと RuntimeException
                return org.springframework.security.core.userdetails.User
                        .withUsername(u.getUsername())
                        .password(u.getPassword()) // ここはハッシュ済み（UserEntity#getPassword）
                        .authorities("USER")
                        .build();
            } catch (RuntimeException e) {
                throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found: " + username);
            }
        };
    }

    // DaoAuthenticationProvider を登録（BCrypt での照合に必須）
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtRequestFilter jwtRequestFilter,
                                                   AuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(c -> {})
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users", "/api/login", "/api/debug/**").permitAll()
                        .anyRequest().authenticated()
                )
                // ★ これを明示（重要）
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, ex1) -> res.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((req, res, ex2) -> res.sendError(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN))
                )
                .addFilterBefore(jwtRequestFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        // 開発中のフロントのオリジンを列挙（Vite=5173, Next=3000）
        cors.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://emortion-journal-frontend.vercel.app/"
        ));
        cors.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cors.setAllowedHeaders(List.of("*")); // もしくは ["Authorization","Content-Type"]
        cors.setExposedHeaders(List.of("Authorization")); // 必要なら
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

}

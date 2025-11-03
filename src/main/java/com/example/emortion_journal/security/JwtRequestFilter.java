package com.example.emortion_journal.security;

import com.example.emortion_journal.model.UserEntity;
import com.example.emortion_journal.service.UserEntryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

public class JwtRequestFilter extends OncePerRequestFilter {
    private final UserEntryService userEntryService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtRequestFilter(UserEntryService userEntryService, JwtTokenUtil jwtTokenUtil) {
        this.userEntryService = userEntryService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        // 素通し
        if ("/api/login".equals(path) || "/api/users".equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");
        System.out.println("[JWT] path=" + path + " authHeader=" + auth);

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7).trim();
            try {
                String username = jwtTokenUtil.getUsernameFromToken(token);
                System.out.println("[JWT] parsed sub=" + username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null
                        && jwtTokenUtil.validateToken(token, username)) {

                    var user = userEntryService.findByUsername(username);
                    var authToken = new UsernamePasswordAuthenticationToken(
                            user.getUsername(), null, java.util.List.of()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[JWT] SecurityContext set for " + username);
                }
            } catch (Exception e) {
                System.out.println("[JWT] validation failed: " + e.getClass().getSimpleName() + " " + e.getMessage());
            }
        } else {
            System.out.println("[JWT] no/invalid Authorization header for path=" + path);
        }

        chain.doFilter(request, response);
    }

}
package com.example.emortion_journal.contoroller;

import com.example.emortion_journal.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final JwtTokenUtil jwtTokenUtil;

    // ✅ コンストラクタインジェクション
    public DebugController(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        return Map.of(
                "authenticated", auth != null,
                "principal", auth != null ? auth.getPrincipal() : null,
                "authorities", auth != null ? auth.getAuthorities() : List.of()
        );
    }

    @GetMapping("/headers")
    public Map<String, Object> headers(HttpServletRequest req) {
        List<String> authHeaders = new ArrayList<>();
        var e = req.getHeaders("Authorization");
        while (e.hasMoreElements()) authHeaders.add(e.nextElement());
        return Map.of(
                "authorizationHeaders", authHeaders,
                "allHeaderNames", Collections.list(req.getHeaderNames())
        );
    }

    @GetMapping("/parse")
    public Map<String, Object> parse(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        var res = new HashMap<String, Object>();
        res.put("receivedAuthorization", authHeader);
        try {
            if (authHeader == null) throw new IllegalArgumentException("missing Authorization");
            String v = authHeader.trim();
            int i = v.toLowerCase().indexOf("bearer ");
            if (i < 0) throw new IllegalArgumentException("no Bearer prefix");
            String token = v.substring(i + "bearer ".length()).trim().replace("\"", "");

            // ✅ JwtTokenUtil を直接使ってトークンを解析
            String subject = jwtTokenUtil.getUsernameFromToken(token);
            res.put("subject", subject);
            res.put("tokenValid", jwtTokenUtil.validateToken(token, subject));
        } catch (Exception ex) {
            res.put("tokenValid", false);
            res.put("error", ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
        return res;
    }
}

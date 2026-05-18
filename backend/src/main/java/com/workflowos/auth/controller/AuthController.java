package com.workflowos.auth.controller;

import com.workflowos.auth.dto.AuthResponse;
import com.workflowos.auth.dto.LoginRequest;
import com.workflowos.auth.dto.RegisterRequest;
import com.workflowos.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication.
 *
 * <pre>
 *   POST /api/auth/register  — create a new account
 *   POST /api/auth/login     — sign in with email + password
 *   POST /api/auth/logout    — invalidate session token
 *   GET  /api/auth/me        — validate token and return current user
 * </pre>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/register */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        AuthResponse response = authService.register(req);
        return ResponseEntity.status(201).body(response);
    }

    /** POST /api/auth/login */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        AuthResponse response = authService.login(req);
        return ResponseEntity.ok(response);
    }

    /** POST /api/auth/logout — expects { "token": "..." } in body */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token != null && !token.isBlank()) {
            authService.logout(token);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /** GET /api/auth/me — Bearer token in Authorization header */
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Missing or invalid Authorization header"));
        }
        String token = authHeader.substring(7);
        var user = authService.validateSession(token);
        return ResponseEntity.ok(Map.of(
            "id",    user.getId(),
            "name",  user.getName(),
            "email", user.getEmail()
        ));
    }

    // ─── Global error handler for this controller ────────────────────────────

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(Map.of("message", ex.getMessage()));
    }
}

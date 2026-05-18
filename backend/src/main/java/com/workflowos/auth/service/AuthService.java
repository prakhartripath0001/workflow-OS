package com.workflowos.auth.service;

import com.workflowos.auth.dto.AuthResponse;
import com.workflowos.auth.dto.LoginRequest;
import com.workflowos.auth.dto.RegisterRequest;
import com.workflowos.auth.entity.UserSession;
import com.workflowos.auth.repository.AuthUserRepository;
import com.workflowos.auth.repository.UserSessionRepository;
import com.workflowos.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Handles registration, login, and logout logic for the auth module.
 *
 * <p>Sessions are stored in the {@code user_sessions} table.
 * A session token is a random UUID; real apps may prefer a signed JWT instead.</p>
 */
@Service
public class AuthService {

    private static final int SESSION_HOURS = 24;

    private final AuthUserRepository    userRepo;
    private final UserSessionRepository sessionRepo;
    private final BCryptPasswordEncoder bcrypt;

    public AuthService(AuthUserRepository userRepo,
                       UserSessionRepository sessionRepo) {
        this.userRepo    = userRepo;
        this.sessionRepo = sessionRepo;
        this.bcrypt      = new BCryptPasswordEncoder();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  REGISTER
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a new user account, hashes the password with BCrypt, and returns
     * a fresh session token on success.
     *
     * @throws IllegalArgumentException if email is already taken or inputs are blank
     */
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        validate(req.getName(),     "Name is required");
        validate(req.getEmail(),    "Email is required");
        validate(req.getPassword(), "Password is required");

        if (req.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (userRepo.existsByEmail(req.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("An account with that email already exists");
        }

        User user = new User();
        user.setName(req.getName().trim());
        user.setEmail(req.getEmail().trim().toLowerCase());
        user.setPasswordHash(bcrypt.encode(req.getPassword()));
        user = userRepo.save(user);

        String token = createSession(user.getId());
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), token, "Registered successfully");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  LOGIN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifies email + password and returns a new session token.
     *
     * @throws IllegalArgumentException on bad credentials
     */
    @Transactional
    public AuthResponse login(LoginRequest req) {
        validate(req.getEmail(),    "Email is required");
        validate(req.getPassword(), "Password is required");

        User user = userRepo.findByEmail(req.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!bcrypt.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = createSession(user.getId());
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), token, "Login successful");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  LOGOUT
    // ─────────────────────────────────────────────────────────────────────────

    /** Invalidates the session identified by {@code token}. */
    @Transactional
    public void logout(String token) {
        sessionRepo.deleteByToken(token);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SESSION VALIDATION (used by other controllers if needed)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the User for a given Bearer token, or throws if invalid/expired.
     */
    @Transactional(readOnly = true)
    public User validateSession(String token) {
        UserSession session = sessionRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Session not found or already logged out"));

        if (!session.isActive()) {
            sessionRepo.deleteByToken(token);
            throw new IllegalArgumentException("Session has expired — please log in again");
        }

        return userRepo.findById(session.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User no longer exists"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private String createSession(UUID userId) {
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setToken(UUID.randomUUID().toString());
        session.setExpiresAt(OffsetDateTime.now().plusHours(SESSION_HOURS));
        return sessionRepo.save(session).getToken();
    }

    private static void validate(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}

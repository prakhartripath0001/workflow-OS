package com.workflowos.auth.handler;

import com.workflowos.auth.repository.AuthUserRepository;
import com.workflowos.auth.repository.UserSessionRepository;
import com.workflowos.auth.entity.UserSession;
import com.workflowos.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Called by Spring Security after a successful Google or GitHub OAuth2 login.
 *
 * <p>Flow:
 * <ol>
 *   <li>Extract user info from the OAuth2 principal</li>
 *   <li>Find existing user by (provider, providerId), or create a new one</li>
 *   <li>Create a session token in user_sessions</li>
 *   <li>Redirect the browser to the frontend with the token in query params</li>
 * </ol>
 * </p>
 */
@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${oauth2.success-redirect-url:http://localhost:5173}")
    private String frontendUrl;

    private static final int SESSION_HOURS = 24;

    private final AuthUserRepository    userRepo;
    private final UserSessionRepository sessionRepo;

    public OAuthSuccessHandler(AuthUserRepository userRepo, UserSessionRepository sessionRepo) {
        this.userRepo    = userRepo;
        this.sessionRepo = sessionRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        // ── Detect provider from the client registration ID ──────────────────
        String registrationId = extractRegistrationId(request);
        User.Provider provider = "github".equalsIgnoreCase(registrationId)
            ? User.Provider.github
            : User.Provider.google;

        // ── Extract profile attributes ────────────────────────────────────────
        String providerId = String.valueOf(
            provider == User.Provider.github
                ? oauthUser.getAttribute("id")
                : oauthUser.getAttribute("sub")
        );
        String email     = oauthUser.getAttribute("email");
        String name      = oauthUser.getAttribute("name");
        String avatarUrl = provider == User.Provider.github
            ? oauthUser.getAttribute("avatar_url")
            : oauthUser.getAttribute("picture");

        // ── Find or create the user ───────────────────────────────────────────
        User user = findOrCreate(provider, providerId, email, name, avatarUrl);

        // ── Create a session ──────────────────────────────────────────────────
        String token = createSession(user.getId());

        // ── Redirect frontend with token + user info ──────────────────────────
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
            .queryParam("wf_token",  token)
            .queryParam("wf_id",     user.getId())
            .queryParam("wf_name",   encodeParam(user.getName()))
            .queryParam("wf_email",  encodeParam(user.getEmail()))
            .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private User findOrCreate(User.Provider provider, String providerId,
                              String email, String name, String avatarUrl) {

        // 1. Match by (provider, providerId) — same OAuth account returning
        Optional<User> byProvider = userRepo.findByProviderAndProviderId(provider, providerId);
        if (byProvider.isPresent()) {
            User u = byProvider.get();
            u.setName(name != null ? name : u.getName());
            u.setAvatarUrl(avatarUrl);
            return userRepo.save(u);
        }

        // 2. Match by email — link OAuth account to existing local account
        if (email != null) {
            Optional<User> byEmail = userRepo.findByEmail(email.toLowerCase());
            if (byEmail.isPresent()) {
                User u = byEmail.get();
                u.setProvider(provider);
                u.setProviderId(providerId);
                u.setAvatarUrl(avatarUrl);
                return userRepo.save(u);
            }
        }

        // 3. New user
        User u = new User();
        u.setName(name != null ? name : "User");
        u.setEmail(email != null ? email.toLowerCase() : providerId + "@" + provider + ".oauth");
        u.setProvider(provider);
        u.setProviderId(providerId);
        u.setAvatarUrl(avatarUrl);
        return userRepo.save(u);
    }

    private String createSession(UUID userId) {
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setToken(UUID.randomUUID().toString());
        session.setExpiresAt(OffsetDateTime.now().plusHours(SESSION_HOURS));
        return sessionRepo.save(session).getToken();
    }

    private static String extractRegistrationId(HttpServletRequest request) {
        // Spring routes to /login/oauth2/code/{registrationId}
        String uri = request.getRequestURI();
        String[] parts = uri.split("/");
        return parts[parts.length - 1];
    }

    private static String encodeParam(String value) {
        return value == null ? "" : value.replace(" ", "+");
    }
}

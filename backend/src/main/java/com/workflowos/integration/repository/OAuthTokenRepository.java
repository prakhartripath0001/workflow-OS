package com.workflowos.integration.repository;

import com.workflowos.integration.entity.OAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OAuthTokenRepository extends JpaRepository<OAuthToken, Long> {
    Optional<OAuthToken> findByUserIdAndProvider(UUID userId, String provider);
}

package com.workflowos.extension.repository;

import com.workflowos.extension.entity.InstalledExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstalledExtensionRepository extends JpaRepository<InstalledExtension, Long> {
    List<InstalledExtension> findByUserId(UUID userId);
    Optional<InstalledExtension> findByUserIdAndExtensionId(UUID userId, String extensionId);
}

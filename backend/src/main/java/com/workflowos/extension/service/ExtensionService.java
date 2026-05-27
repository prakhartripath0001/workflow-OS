package com.workflowos.extension.service;

import com.workflowos.extension.dto.ExtensionManifestRequest;
import com.workflowos.extension.entity.Extension;
import com.workflowos.extension.entity.InstalledExtension;

import java.util.List;
import java.util.UUID;

public interface ExtensionService {
    Extension publish(ExtensionManifestRequest request);
    InstalledExtension install(UUID userId, String extensionId);
    InstalledExtension setEnabled(UUID userId, String extensionId, boolean enabled);
    List<InstalledExtension> installed(UUID userId);
    List<Extension> marketplace();
}

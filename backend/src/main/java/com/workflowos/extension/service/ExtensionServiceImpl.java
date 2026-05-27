package com.workflowos.extension.service;

import com.workflowos.common.exception.ResourceNotFoundException;
import com.workflowos.extension.dto.ExtensionManifestRequest;
import com.workflowos.extension.entity.Extension;
import com.workflowos.extension.entity.InstalledExtension;
import com.workflowos.extension.repository.ExtensionRepository;
import com.workflowos.extension.repository.InstalledExtensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExtensionServiceImpl implements ExtensionService {
    private final ExtensionRepository extensionRepository;
    private final InstalledExtensionRepository installedExtensionRepository;

    @Override
    @Transactional
    public Extension publish(ExtensionManifestRequest request) {
        Extension extension = extensionRepository.findById(request.id()).orElseGet(Extension::new);
        extension.setId(request.id());
        extension.setName(request.name());
        extension.setVersion(request.version());
        extension.setDescription(request.description());
        extension.setPublisherId(request.publisherId());
        extension.setEntrypoint(request.entrypoint());
        extension.setPermissions(request.permissions());
        extension.setManifest(request.manifest());
        extension.setSignature(request.signature());
        return extensionRepository.save(extension);
    }

    @Override
    @Transactional
    public InstalledExtension install(UUID userId, String extensionId) {
        Extension extension = extensionRepository.findById(extensionId)
                .orElseThrow(() -> new ResourceNotFoundException("Extension not found: " + extensionId));
        InstalledExtension installed = installedExtensionRepository
                .findByUserIdAndExtensionId(userId, extensionId)
                .orElseGet(InstalledExtension::new);
        installed.setUserId(userId);
        installed.setExtensionId(extensionId);
        installed.setVersion(extension.getVersion());
        installed.setEnabled(true);
        return installedExtensionRepository.save(installed);
    }

    @Override
    @Transactional
    public InstalledExtension setEnabled(UUID userId, String extensionId, boolean enabled) {
        InstalledExtension installed = installedExtensionRepository.findByUserIdAndExtensionId(userId, extensionId)
                .orElseThrow(() -> new ResourceNotFoundException("Installed extension not found: " + extensionId));
        installed.setEnabled(enabled);
        return installedExtensionRepository.save(installed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstalledExtension> installed(UUID userId) {
        return installedExtensionRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Extension> marketplace() {
        return extensionRepository.findAll();
    }
}

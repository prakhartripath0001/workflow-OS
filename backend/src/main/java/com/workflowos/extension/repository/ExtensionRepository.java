package com.workflowos.extension.repository;

import com.workflowos.extension.entity.Extension;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtensionRepository extends JpaRepository<Extension, String> {
}

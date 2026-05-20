package com.workflowos.command.repository;

import com.workflowos.command.entity.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
    Optional<Command> findByName(String name);
    List<Command> findByIsActiveTrue();
}

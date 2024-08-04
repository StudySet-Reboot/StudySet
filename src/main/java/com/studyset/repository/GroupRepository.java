package com.studyset.repository;

import com.studyset.domain.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Page<Group> findGroupsByGroupNameIsContaining(String keyword, Pageable pageable);

    Optional<Group> findByGroupNameAndCode(String groupName, String code);

    Optional<Group> findGroupById(Long id);
}

package com.studyset.repository;

import com.studyset.domain.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Page<Group> findGroupsByGroupNameIsContaining(String keyword, Pageable pageable);
}

package com.studyset.repository;

import com.studyset.domain.Dues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DuesRepository extends JpaRepository<Dues, Long> {
    List<Dues> findAllByGroupId(Long groupId);
}

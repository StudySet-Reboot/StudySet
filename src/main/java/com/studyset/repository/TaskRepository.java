package com.studyset.repository;

import com.studyset.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>  {
    // 그룹 내 모든 과제 조회
    List<Task> findByGroupId(@Param("groupId") Long groupId);
    // 특정 과제 조회
    Optional<Task> findById(@Param("taskId") Long taskId);
}
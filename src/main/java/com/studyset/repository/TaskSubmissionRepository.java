package com.studyset.repository;

import com.studyset.domain.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {
    // 특정 과제의 그룹원 과제 조회
    @Query("SELECT ts FROM TaskSubmission ts WHERE ts.task.id = :taskId")
    List<TaskSubmission> findByTaskId(@Param("taskId") Long taskId);
}

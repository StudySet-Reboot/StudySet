package com.studyset.repository;

import com.studyset.domain.Task;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>  {
    // 그룹 내 모든 과제 조회
    List<Task> findByGroupId(@Param("groupId") Long groupId);
    // 특정 과제 조회
    Optional<Task> findById(@Param("taskId") Long taskId);
    // 현재 날짜 포함 과제 조회
    @Query("SELECT t FROM Task t WHERE t.group.id = :groupId AND " +
            "(t.startTime IS NULL OR t.startTime <= :currentDate) AND " +
            "(t.endTime IS NULL OR t.endTime >= :currentDate)")
    List<Task> findCurrentTasksByGroupId(@Param("groupId") Long groupId, @Param("currentDate") LocalDate currentDate);
    // 과제 기간 필터링 조회
    @Query(value = "SELECT t.id, t.group.id, t.taskName, t.description, t.startTime, t.endTime, " +
            "CASE " +
            "WHEN CURRENT_DATE BETWEEN t.startTime AND t.endTime THEN 'ongoing' " +
            "WHEN CURRENT_DATE < t.startTime THEN 'upcoming' " +
            "ELSE 'completed' " +
            "END AS task_status " +
            "FROM Task t WHERE t.group.id = :groupId")
    List<Object[]> findAllWithStatus(@Param("groupId") Long groupId);
}
package com.studyset.repository;

import com.studyset.domain.Group;
import com.studyset.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    public List<Schedule> findSchedulesByGroup_IdAndStartTimeBetween(long groupId, LocalDateTime startTime, LocalDateTime startTime2);
}

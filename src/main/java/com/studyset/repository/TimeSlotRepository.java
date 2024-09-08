package com.studyset.repository;

import com.studyset.domain.TimeSlot;
import com.studyset.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    public Optional<TimeSlot> findTimeSlotByUserAndGroupId(User user, Long groupId);

    public void deleteAllByUserAndGroupId(User user, Long groupId);

    public List<TimeSlot> findTimeSlotByGroupId(Long groupId);
}

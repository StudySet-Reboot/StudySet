package com.studyset.service;

import com.studyset.api.exception.GroupNotExist;
import com.studyset.domain.Group;
import com.studyset.domain.Schedule;
import com.studyset.dto.schedule.Event;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.ScheduleRepository;
import com.studyset.web.form.ScheduleCreateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public void addSchedule(ScheduleCreateForm scheduleCreateForm, long groupId) {
        Group group = groupRepository.findGroupById(groupId)
                .orElseThrow(GroupNotExist::new);
        Schedule schedule = scheduleCreateForm.toEntity();
        schedule.addGroup(group);
        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<Event> getGroupSchedule(long groupId, Integer year, Integer month) {
        if(year == null) year = LocalDateTime.now().getYear();
        if(month == null) month = LocalDateTime.now().getMonthValue();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        List<Schedule> scheduleList = scheduleRepository.findSchedulesByGroup_IdAndStartTimeBetween(groupId, start, end);
        return scheduleList.stream().map(Schedule::toEvent)
                .toList();
    }
}

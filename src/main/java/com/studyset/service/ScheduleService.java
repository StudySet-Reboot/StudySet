package com.studyset.service;

import com.studyset.api.exception.GroupNotExist;
import com.studyset.api.request.schedule.ScheduleEditRequest;
import com.studyset.api.request.schedule.TimeAdjustRequest;
import com.studyset.domain.Group;
import com.studyset.domain.Schedule;
import com.studyset.api.response.schedule.Event;
import com.studyset.domain.TimeSlot;
import com.studyset.domain.User;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.ScheduleRepository;
import com.studyset.api.request.schedule.ScheduleCreateForm;
import com.studyset.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.studyset.api.request.schedule.TimeAdjustRequest.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final TimeSlotRepository timeSlotRepository;

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

    @Transactional
    public Event editSchedule(Long scheduleId, ScheduleEditRequest scheduleEditRequest) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(IllegalArgumentException::new);
        schedule.edit(scheduleEditRequest);
        scheduleRepository.save(schedule);
        return schedule.toEvent();
    }

    @Transactional(readOnly = true)
    public boolean[][] getUsersAvailableTime(User user, Long groupId) {
        Group group = groupRepository.findGroupById(groupId)
                .orElseThrow(GroupNotExist::new);
        TimeSlot timeSlot = timeSlotRepository.findTimeSlotByUserAndGroupId(user, groupId)
                .orElse(createNewTimeSlot(user, group));
        return timeSlot.getTimeSlots();
    }

    @Transactional
    public void addTimeSlots(User user, Long groupId, TimeAdjustRequest timeAdjustRequest) {
        Group group = groupRepository.findGroupById(groupId)
                .orElseThrow(GroupNotExist::new);
        TimeSlot timeSlot = timeSlotRepository.findTimeSlotByUserAndGroupId(user, groupId)
                .orElse(createNewTimeSlot(user, group));
        boolean[][] timeslotList = new boolean[24][7];

        for(TimeSlotData data: timeAdjustRequest.getList()){
            int day = data.getDay();
            int time = data.getTime();
            timeslotList[time][day] = true;
        }
        timeSlot.setTimeSlots(timeslotList);
        timeSlotRepository.save(timeSlot);
    }

    public TimeSlot createNewTimeSlot(User user, Group group){
        TimeSlot timeSlot = TimeSlot
                .builder()
                .user(user)
                .group(group)
                .build();
        timeSlot.setTimeSlots(new boolean[24][7]);
        return timeSlot;
    }
}

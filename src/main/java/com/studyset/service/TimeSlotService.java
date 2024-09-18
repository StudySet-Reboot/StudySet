package com.studyset.service;

import com.studyset.exception.GroupNotExist;
import com.studyset.api.request.schedule.TimeAdjustRequest;
import com.studyset.domain.Group;
import com.studyset.domain.TimeSlot;
import com.studyset.domain.User;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimeSlotService {

    private final GroupRepository groupRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional(readOnly = true)
    public int[][] getAvailableTime(Long userId, Long groupId) {
        if (userId == null) {
            return getGroupAvailableTime(groupId);
        }

        Optional<TimeSlot> timeSlot = timeSlotRepository.findTimeSlotByUserIdAndGroupId(userId, groupId);
        return timeSlot.map(TimeSlot::getTimeSlots).orElse(new int[24][7]);
    }

    @Transactional(readOnly = true)
    public int[][] getGroupAvailableTime(Long groupId) {
        List<TimeSlot> timeSlotList = timeSlotRepository.findTimeSlotByGroupId(groupId);
        int[][] arrTimeslots = combineTimeSlots(timeSlotList);
        return arrTimeslots;
    }

    @Transactional
    public void addTimeSlots(User user, Long groupId, TimeAdjustRequest timeAdjustRequest) {
        Group group = groupRepository.findGroupById(groupId)
                .orElseThrow(GroupNotExist::new);
        TimeSlot timeSlot = timeSlotRepository.findTimeSlotByUserAndGroupId(user, groupId)
                .orElse(createNewTimeSlot(user, group));

        boolean[][] timeslotList = new boolean[24][7];
        for(TimeAdjustRequest.TimeSlotData data: timeAdjustRequest.getList()){
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

    private int[][] combineTimeSlots(List<TimeSlot> timeSlotList) {
        int[][] combinedSlots = new int[24][7];
        for (TimeSlot slot : timeSlotList) {
            int[][] slots = slot.getTimeSlots();
            for (int hour = 0; hour < 24; hour++) {
                for (int day = 0; day < 7; day++) {
                    combinedSlots[hour][day] += slots[hour][day];
                }
            }
        }
        return combinedSlots;
    }

}

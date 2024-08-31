package com.studyset.service;

import com.studyset.api.exception.GroupNotExist;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class TimeSlotService {

    private final GroupRepository groupRepository;
    private final TimeSlotRepository timeSlotRepository;

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
}

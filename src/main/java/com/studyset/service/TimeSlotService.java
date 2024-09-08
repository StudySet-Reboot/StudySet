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

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimeSlotService {

    private final GroupRepository groupRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional(readOnly = true)
    public int[][] getGroupAvailableTime(Long groupId) {
        List<TimeSlot> timeSlotList = timeSlotRepository.findTimeSlotByGroupId(groupId);
        int[][] arrTimeslots = combineTimeSlots(timeSlotList);
        return arrTimeslots;
    }

    @Transactional(readOnly = true)
    public int[][] getUsersAvailableTime(User user, Long groupId) {
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

    public static int[][] combineTimeSlots(List<TimeSlot> timeSlotList) {
        if(timeSlotList.isEmpty()) {
            return new int[24][7];
        }
        StringBuilder combinedSlots = new StringBuilder(timeSlotList.get(0).getAvailTime());
        for (int i = 1; i < timeSlotList.size(); i++) {
            String currentSlot = timeSlotList.get(i).getAvailTime();
            for (int j = 0; j < combinedSlots.length(); j++) {
                int sum = (combinedSlots.charAt(j) - '0') + (currentSlot.charAt(j) - '0');
                combinedSlots.setCharAt(j, (char) (sum + '0'));
            }
        }
        return convertStringToIntArray(combinedSlots.toString());
    }

    public static int[][] convertStringToIntArray(String timeSlotString) {
        int[][] timeSlots = new int[24][7];  // 24시간, 7일
        for (int hour = 0; hour < 24; hour++) {
            for (int day = 0; day < 7; day++) {
                timeSlots[hour][day] = timeSlotString.charAt(hour * 7 + day) - '0';  // char to int 변환
            }
        }
        return timeSlots;
    }

}

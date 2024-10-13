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

    /**
     * 특정 사용자의 그룹 내 가능한 시간대 정보를 반환합니다.
     *
     * @param userId 시간대를 조회할 사용자의 ID
     * @param groupId 시간대를 조회할 그룹의 ID
     * @return 사용자의 시간 슬롯이 포함된 24x7 배열, 사용자가 없는 경우 0으로 채워진 배열 반환
     */
    @Transactional(readOnly = true)
    public int[][] getAvailableTime(Long userId, Long groupId) {
        if (userId == null) {
            return getGroupAvailableTime(groupId);
        }

        Optional<TimeSlot> timeSlot = timeSlotRepository.findTimeSlotByUserIdAndGroupId(userId, groupId);
        return timeSlot.map(TimeSlot::getTimeSlots).orElse(new int[24][7]);
    }

    /**
     * 그룹의 전체 멤버가 사용 가능한 시간대 정보를 반환합니다.
     *
     * @param groupId 시간대를 조회할 그룹의 ID
     * @return 그룹의 시간 슬롯이 결합된 24x7 배열
     */
    @Transactional(readOnly = true)
    public int[][] getGroupAvailableTime(Long groupId) {
        List<TimeSlot> timeSlotList = timeSlotRepository.findTimeSlotByGroupId(groupId);
        int[][] arrTimeslots = combineTimeSlots(timeSlotList);
        return arrTimeslots;
    }

    /**
     * 사용자의 Time Slot을 추가하거나 수정합니다.
     *
     * @param user 시간 슬롯을 추가할 사용자
     * @param groupId 시간 슬롯을 추가할 그룹의 ID
     * @param timeAdjustRequest 추가할 시간 슬롯 데이터가 포함된 요청 객체
     * @throws GroupNotExist 주어진 ID의 그룹이 존재하지 않을 때 발생하는 예외
     */
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

    /**
     * 새로운 사용자의 Time Slot을 생성합니다.
     *
     * @param user 시간 슬롯을 생성할 사용자
     * @param group 시간 슬롯이 속할 그룹
     * @return 새로 생성된 TimeSlot
     */
    public TimeSlot createNewTimeSlot(User user, Group group){
        TimeSlot timeSlot = TimeSlot
                .builder()
                .user(user)
                .group(group)
                .build();
        timeSlot.setTimeSlots(new boolean[24][7]);
        return timeSlot;
    }

    /**
     * 여러 시간 슬롯을 결합하여 그룹의 사용 가능한 시간대를 생성합니다.
     *
     * @param timeSlotList 결합할 TimeSlot 객체 목록
     * @return 결합된 시간 슬롯이 포함된 24x7 배열
     */
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

package com.studyset.service;

import com.studyset.api.request.schedule.TimeAdjustRequest;
import com.studyset.domain.Group;
import com.studyset.domain.TimeSlot;
import com.studyset.domain.User;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.TimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimeSlotServiceTest {
    @InjectMocks
    private TimeSlotService timeSlotService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    private User user;
    private Group group;
    private TimeSlot timeSlot;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .email("test@test.com")
                .name("test")
                .build();

        group = Group.builder()
                .groupName("test group")
                .build();
        timeSlot = TimeSlot.builder().user(user).group(group).build();
        timeSlot.setTimeSlots(new boolean[24][7]);
    }

    @Test
    @DisplayName("이미 제출한 적 있는 User의 가능한 스캐줄표 가져오기")
    void getUsersAvailableTime_existingTimeSlot() {
        Long testGroupId = 1L;
        Long testUserId = 1L;
        when(timeSlotRepository.findTimeSlotByUserIdAndGroupId(testUserId, testGroupId))
                .thenReturn(Optional.of(timeSlot));

        int[][] result = timeSlotService.getAvailableTime(testUserId, testGroupId);

        assertNotNull(result);
        verify(timeSlotRepository, times(1)).findTimeSlotByUserIdAndGroupId(testUserId, testGroupId);
    }

    @Test
    @DisplayName("아직 제출한 적 없는 User의 가능한 스캐줄표는 새롭게 생성됨")
    void getUsersAvailableTime_nonExistingTimeSlot() {
        Long testGroupId = 1L;
        Long testUserId = 1L;
        // 아직 존재하지 않는 경우
        when(timeSlotRepository.findTimeSlotByUserIdAndGroupId(testUserId, testGroupId))
                .thenReturn(Optional.empty());

        int[][] result = timeSlotService.getAvailableTime(testUserId, testGroupId);
        assertNotNull(result);
        assertEquals(0, result[1][1]);
    }

    @Test
    @DisplayName("시간표 작성 및 수정 성공")
    void addTimeSlots_success() {
        Long testGroupId = 1L;
        Long testUserId = 1L;
        when(groupRepository.findGroupById(testGroupId))
                .thenReturn(Optional.of(group));
        when(timeSlotRepository.findTimeSlotByUserAndGroupId(user, testGroupId))
                .thenReturn(Optional.of(timeSlot));

        TimeAdjustRequest request = new TimeAdjustRequest();
        request.setList(Arrays.asList(
                new TimeAdjustRequest.TimeSlotData(0, 1),
                new TimeAdjustRequest.TimeSlotData(1, 2)
        ));

        timeSlotService.addTimeSlots(user, testGroupId, request);

        verify(timeSlotRepository, times(1)).save(timeSlot);

        int[][] savedTimeSlots = timeSlot.getTimeSlots();
        assertEquals(1, savedTimeSlots[1][0]);
        assertEquals(1, savedTimeSlots[2][1]);
    }

    @Test
    @DisplayName("그룹에 가입한 유저들의 합산 가능 시간표 가져오기")
    void getAllMembersTimeSlots_success() {
        //given
        TimeSlot timeSlot1 = TimeSlot.builder().user(user).group(group).build();
        TimeSlot timeSlot2 = TimeSlot.builder().user(user).group(group).build();
        boolean[][] list = new boolean[24][7];
        list[0][0] = true;
        timeSlot1.setTimeSlots(list);
        list[2][1] = true;
        timeSlot2.setTimeSlots(list);

        when(timeSlotRepository.findTimeSlotByGroupId(1L))
                .thenReturn(List.of(timeSlot1, timeSlot2));

        //when
        int[][] result = timeSlotService.getGroupAvailableTime(1L);

        //then
        assertNotNull(result);
        assertEquals(2, result[0][0]);
        assertEquals(1, result[2][1]);
    }

}
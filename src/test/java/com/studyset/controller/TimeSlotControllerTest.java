package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.service.JoinService;
import com.studyset.service.TimeSlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TimeSlotControllerTest {

    @Mock
    private TimeSlotService timeSlotService;

    @Mock
    private JoinService joinService;

    private MockMvc mockMvc;

    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TimeSlotController(timeSlotService, joinService)).build();
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .build();
    }

    @Test
    @DisplayName("스캐줄 조정 페이지에서는 가능한 시간 배열 반환")
    void showAdjustPage_shouldReturnAdjustPageWithAvailableTimes() throws Exception {
        Long groupId = 1L;
        int[][] availableTime = new int[24][7];
        when(timeSlotService.getGroupAvailableTime(groupId)).thenReturn(availableTime);
        when(joinService.getUserByGroupId(groupId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/groups/{groupId}/timetables/view", groupId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("times", "userList"))
                .andExpect(model().attribute("times", availableTime));
    }

    @Test
    @DisplayName("특정 사용자의 스캐줄 조정표 조회")
    void getUserTable_shouldReturnUserTimeTable() throws Exception {
        Long groupId = 1L;
        int[][] availableTime = new int[24][7];
        when(timeSlotService.getAvailableTime(user.getId(), groupId)).thenReturn(availableTime);

        mockMvc.perform(get("/groups/{groupId}/schedules/adjust", groupId)
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("times"))
                .andExpect(model().attribute("times", availableTime));
    }

}

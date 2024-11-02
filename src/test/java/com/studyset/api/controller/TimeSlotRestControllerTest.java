package com.studyset.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyset.api.request.schedule.TimeAdjustRequest;
import com.studyset.service.TimeSlotService;
import com.studyset.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TimeSlotRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TimeSlotService timeSlotService;

    @Autowired
    private ObjectMapper mapper;

    private User user;

    @InjectMocks
    private TimeSlotRestController timeSlotRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(timeSlotRestController).build();
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .build();
    }

    @Test
    @DisplayName("스캐줄 조정표 제출 성공")
    void submitTimeTable_Success() throws Exception {
        Long groupId = 1L;
        TimeAdjustRequest.TimeSlotData timeSlotData1 = new TimeAdjustRequest.TimeSlotData(1, 10);
        TimeAdjustRequest.TimeSlotData timeSlotData2 = new TimeAdjustRequest.TimeSlotData(2, 11);
        TimeAdjustRequest request = new TimeAdjustRequest(Arrays.asList(timeSlotData1, timeSlotData2));

        mockMvc.perform(post("/groups/{groupId}/timetables", groupId)
                        .sessionAttr("user", user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}

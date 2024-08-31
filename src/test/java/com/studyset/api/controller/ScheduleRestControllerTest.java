package com.studyset.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyset.api.exception.GroupNotExist;
import com.studyset.api.request.schedule.ScheduleCreateForm;
import com.studyset.api.request.schedule.ScheduleEditRequest;
import com.studyset.api.response.schedule.Event;
import com.studyset.controller.ExceptionController;
import com.studyset.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ScheduleRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ScheduleRestController(scheduleService))
                .setControllerAdvice(new ExceptionController())
                .build();
    }

    @Test
    void testShowSchedule() throws Exception {
        List<Event> eventList = Collections.singletonList(Event.builder().id(1L).build());
        when(scheduleService.getGroupSchedule(anyLong(), any(), any())).thenReturn(eventList);

        mockMvc.perform(get("/api/groups/1/schedules/events")
                        .param("year", "2024")
                        .param("month", "8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
    @Test
    @DisplayName("새로운 Schedule 생성 성공 시 응답은 Created")
    void testSuccessCreateSchedule() throws Exception {
        ScheduleCreateForm scheduleCreateForm = new ScheduleCreateForm();
        scheduleCreateForm.setTitle("Event");
        scheduleCreateForm.setStartDate(LocalDateTime.now());
        String json = mapper.writeValueAsString(scheduleCreateForm);

        mockMvc.perform(post("/api/groups/1/schedules/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("제목 없는 Schedule 생성 실패 시 응답은 BAD_REQUEST")
    void testFailCreateSchedule_BadRequest() throws Exception {
        ScheduleCreateForm scheduleCreateForm = new ScheduleCreateForm();
        scheduleCreateForm.setTitle("");
        String json = mapper.writeValueAsString(scheduleCreateForm);
        doThrow(new GroupNotExist())
                .when(scheduleService).addSchedule(any(ScheduleCreateForm.class), anyLong());

        mockMvc.perform(post("/api/groups/2/schedules/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("시작 날짜 없는 Schedule 생성 실패 시 응답은 BAD_REQUEST")
    void testFailCreateSchedule_BadRequest2() throws Exception {
        ScheduleCreateForm scheduleCreateForm = new ScheduleCreateForm();
        scheduleCreateForm.setTitle("Test");
        String json = mapper.writeValueAsString(scheduleCreateForm);
        doThrow(new GroupNotExist())
                .when(scheduleService).addSchedule(any(ScheduleCreateForm.class), anyLong());

        mockMvc.perform(post("/api/groups/2/schedules/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않은 그룹에 새로운 Schedule 생성 실패 시 응답은 NotExist")
    void testFailCreateSchedule_GroupNotExist() throws Exception {
        ScheduleCreateForm scheduleCreateForm = new ScheduleCreateForm();
        scheduleCreateForm.setTitle("Event");
        scheduleCreateForm.setStartDate(LocalDateTime.now());

        String json = mapper.writeValueAsString(scheduleCreateForm);
        doThrow(new GroupNotExist())
                .when(scheduleService).addSchedule(any(ScheduleCreateForm.class), anyLong());

        mockMvc.perform(post("/api/groups/2/schedules/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 그룹이 존재하지 않습니다"));
    }

    @Test
    @DisplayName("Schedule 수정 성공 테스트")
    void testSuccessEditSchedule() throws Exception {
        Event event = Event.builder().id(1L).build();
        ScheduleEditRequest editRequest = new ScheduleEditRequest();
        editRequest.setTitle("Edit Title");
        editRequest.setStartDate(LocalDateTime.now());
        String json = mapper.writeValueAsString(editRequest);

        when(scheduleService.editSchedule(anyLong(), any())).thenReturn(event);

        mockMvc.perform(put("/api/groups/1/schedules/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(1));
    }

    @Test
    @DisplayName("Schedule 수정 시 제목이 없으면 실패, 응답은 BAD_REQUEST")
    void testFailEditSchedule_BadRequest1() throws Exception {
        ScheduleEditRequest editRequest = new ScheduleEditRequest();
        editRequest.setTitle(null);
        String json = mapper.writeValueAsString(editRequest);

        mockMvc.perform(put("/api/groups/1/schedules/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Schedule 수정 시 시작 날짜 없으면 실패, 응답은 BAD_REQUEST")
    void testFailEditSchedule_BadRequest2() throws Exception {
        ScheduleEditRequest editRequest = new ScheduleEditRequest();
        editRequest.setTitle("Test");
        String json = mapper.writeValueAsString(editRequest);

        mockMvc.perform(put("/api/groups/1/schedules/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Schedule 삭제 성공 테스트")
    void testSuccessDeleteSchedule() throws Exception {
        mockMvc.perform(delete("/api/groups/1/schedules/events/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

}

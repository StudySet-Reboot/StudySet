package com.studyset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyset.domain.enumerate.GroupCategory;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.service.GroupService;
import com.studyset.service.TaskService;
import com.studyset.web.form.TaskCreateForm;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Before("")
    public void setUp() {
        openMocks(this); // Mockito 초기화
    }

    @Test
    public void testCreate() throws Exception {
        GroupDto groupDto1 = new GroupDto();
        groupDto1.setId(1L);
        groupDto1.setGroupName("spring");
        groupDto1.setCode("123456");
        groupDto1.setCategory(GroupCategory.valueOf("DESIGN"));
        groupDto1.setDescription("aa");

        LocalDate dueDateTime = LocalDate.of(2024, 8, 22);

        TaskDto taskDto = TaskDto.builder()
                .taskName("example Task")
                .groupId(groupDto1.getId())
                .description("discription")
                .endTime(dueDateTime)
                .build();

        TaskCreateForm form = new TaskCreateForm();
        form.setTaskName("example Task");
        form.setGroupId(2L);
        form.setDescription("description");
        form.setEndTime(dueDateTime);

        given(taskService.addTask(form)).willReturn(taskDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/groups/task/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

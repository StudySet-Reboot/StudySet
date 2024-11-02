package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.dto.user.UserDto;
import com.studyset.exception.hanlder.RestExceptionHandler;
import com.studyset.service.JoinService;
import com.studyset.service.TaskService;
import com.studyset.service.TaskSubmissionService;
import com.studyset.web.form.TaskCreateForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private JoinService joinService;

    @Mock
    private TaskSubmissionService taskSubmissionService;

    private GroupDto group;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();

        // 테스트용 데이터 설정
        group = new GroupDto();
        group.setId(1L);
        group.setGroupName("Test Group");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
    }

    @Test
    @DisplayName("그룹의 과제 메인페이지로 이동")
    void taskMain() throws Exception {
        // given
        TaskDto taskDto = new TaskDto("task");
        List<TaskDto> taskList = Collections.singletonList(taskDto);
        when(taskService.getAllTasksWithStatus(anyLong())).thenReturn(taskList);

        // when & then
        mockMvc.perform(get("/groups/1/task")
                        .sessionAttr("group", group))
                .andExpect(status().isOk())
                .andExpect(view().name("/thyme/task/taskMain"))
                .andExpect(model().attributeExists("group"))
                .andExpect(model().attributeExists("taskList"));
    }

    @Test
    @DisplayName("과제를 생성합니다.")
    void addTask() throws Exception {
        // given
        TaskDto newTask = new TaskDto("task");
        when(taskService.addTask(any(TaskCreateForm.class))).thenReturn(newTask);

        // when & then
        mockMvc.perform(post("/groups/task/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "New Task")
                        .sessionAttr("group", group))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newTask").exists());
    }

    @Test
    @DisplayName("해당 과제의 상세 페이지로 이동합니다.")
    void taskDetail() throws Exception {
        // given
        Long taskId = 1L;
        TaskDto task = new TaskDto("task");
        task.setId(taskId);
        task.setEndTime(LocalDate.from(LocalDateTime.now()));

        List<UserDto> userList = Collections.singletonList(new UserDto());
        List<TaskSubmissionDto> taskSubmissionList = Collections.emptyList();

        when(joinService.getUserByGroupId(anyLong())).thenReturn(userList);
        when(taskService.getTaskDetailByTaskId(taskId)).thenReturn(task);
        when(taskSubmissionService.getTaskSubmissionById(taskId)).thenReturn(taskSubmissionList);

        // when & then
        mockMvc.perform(get("/groups/1/1/task-detail")
                        .sessionAttr("group", group)
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(view().name("thyme/task/taskDetail"))
                .andExpect(model().attributeExists("group"))
                .andExpect(model().attributeExists("userList"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("taskSubmissionMap"));
    }
}

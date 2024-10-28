package com.studyset.controller;

import com.studyset.domain.Task;
import com.studyset.domain.TaskSubmission;
import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.task.CommentDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.exception.hanlder.RestExceptionHandler;
import com.studyset.service.CommentService;
import com.studyset.service.TaskService;
import com.studyset.service.TaskSubmissionService;
import com.studyset.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskSubmissionControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private TaskSubmissionController taskSubmissionController;

    @Mock
    private TaskSubmissionService taskSubmissionService;
    @Mock
    private TaskService taskService;
    @Mock
    private CommentService commentService;
    @Mock
    private UserService userService;

    private GroupDto group;
    private Task task;
    private User user;
    private TaskSubmission taskSubmission;
    private TaskSubmissionDto taskSubmissionDto;
    private TaskDto taskDto;
    private List<CommentDto> commentList;
    private Map<Long, User> usersMap;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskSubmissionController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();

        user = new User();
        user.setId(1L);

        group = new GroupDto();
        group.setId(1L);
        group.setGroupName("Test Group");

        task = new Task();
        task.setId(1L);
        task.setGroup(group.toGroup());
        task.setEndTime(LocalDate.now().plusDays(1));
        ReflectionTestUtils.setField(task, "updatedDate", LocalDateTime.now());

        taskDto = task.toDto();

        taskSubmission = new TaskSubmission();
        taskSubmission.setId(1L);
        taskSubmission.setTask(task);
        taskSubmission.setUser(user);
        taskSubmission.setFilePath("test-file-path.txt");
        ReflectionTestUtils.setField(taskSubmission, "updatedDate", LocalDateTime.now());

        taskSubmissionDto = taskSubmission.toDto();

        commentList = new ArrayList<>();
        usersMap = new HashMap<>();
    }

    @Test
    @DisplayName("과제 미제출 시 제출페이지 이동")
    void taskSubmissionMain() throws Exception {
        // given
        when(taskSubmissionService.getTaskSubmission(anyLong(), anyLong())).thenReturn(null);
        when(taskService.getTaskDetailByTaskId(anyLong())).thenReturn(taskDto);

        // when & then
        mockMvc.perform(get("/groups/1/tasks/1/submit-modify")
                        .sessionAttr("user", user)
                        .sessionAttr("group", group))
                .andExpect(status().isOk())
                // 과제 제출 안 했으니 null로
                .andExpect(model().attribute("task", nullValue()))
                .andExpect(model().attribute("taskDto", taskDto))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("group", group))
                .andExpect(view().name("/thyme/task/userTaskSubmit"));
    }

    @Test
    @DisplayName("과제 기제출 시 수정페이지 이동")
    void taskSubmissionModify() throws Exception {
        // given
        when(taskSubmissionService.getTaskSubmission(anyLong(), anyLong())).thenReturn(taskSubmissionDto);
        when(taskService.getTaskDetailByTaskId(anyLong())).thenReturn(taskDto);

        // when & then
        mockMvc.perform(get("/groups/1/tasks/1/submit-modify")
                        .sessionAttr("user", user)
                        .sessionAttr("group", group))
                .andExpect(status().isOk())
                .andExpect(model().attribute("task", taskSubmissionDto))
                .andExpect(model().attribute("taskDto", taskDto))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("group", group))
                .andExpect(view().name("/thyme/task/modifyUserTaskSubmit"));
    }

    @Test
    @DisplayName("특정 유저의 제출물 페이지로 이동")
    void userTaskMain() throws Exception {
        // given
        when(taskService.getTaskDetailByTaskId(1L)).thenReturn(taskDto);
        when(taskSubmissionService.getTaskSubmission(1L, 1L)).thenReturn(taskSubmissionDto);
        when(commentService.getCommentBySubmissionId(taskSubmissionDto.getId())).thenReturn(commentList);
        when(userService.findUsersByIds(anyList())).thenReturn(usersMap);

        // when & then
        mockMvc.perform(get("/groups/1/tasks/1/1/usertask")
                        .sessionAttr("user", user)
                        .sessionAttr("group", group))
                .andExpect(status().isOk())
                .andExpect(model().attribute("task", taskSubmissionDto))
                .andExpect(model().attribute("taskDto", taskDto))
                .andExpect(model().attribute("commentList", commentList))
                .andExpect(model().attribute("usersMap", usersMap))
                .andExpect(view().name("/thyme/task/userTask"));
    }

    @Test
    @DisplayName("유저 제출물의 파일 다운로드")
    void fileDownload() throws Exception {
        // given
        String filePath = "test-file-path.txt";
        ResponseEntity mockResponse = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + filePath + "\"")
                .body("Sample file content".getBytes());

        when(taskSubmissionService.findTaskSubmission(1L)).thenReturn(taskSubmissionDto);
        when(taskSubmissionService.downloadFile(filePath)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/groups/download/{taskSubmissionId}", 1L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + filePath + "\""))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }
}

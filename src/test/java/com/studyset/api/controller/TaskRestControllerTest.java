package com.studyset.api.controller;

import com.studyset.domain.User;
import com.studyset.exception.ForbiddenAccess;
import com.studyset.exception.TaskNotExist;
import com.studyset.exception.hanlder.RestExceptionHandler;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.service.TaskSubmissionService;
import com.studyset.web.form.TaskSubmissionEditForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.studyset.exception.response.ErrorCode.UNAUTHORIZED_SUBMISSION_ACCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskSubmissionService taskSubmissionService;

    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TaskSubmissionRestController(taskSubmissionService))
                .setControllerAdvice(new RestExceptionHandler())
                .build();

        user = new User();
        user.setId(1L);
    }

    @Test
    @DisplayName("Submission 수정 성공 테스트")
    void testSuccessEditSchedule() throws Exception {
        TaskSubmissionEditForm form = new TaskSubmissionEditForm();
        form.setContent("Updated content");
        TaskSubmissionDto taskSubmissionDto = new TaskSubmissionDto();
        taskSubmissionDto.setContents("Updated content");

        when(taskSubmissionService.editTaskSubmission(anyLong(), any(TaskSubmissionEditForm.class), any(), anyLong()))
                .thenReturn(taskSubmissionDto);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        mockMvc.perform(multipart("/groups/1/submission/1")
                        .file(file)
                        .param("content", "Updated content")
                        .sessionAttr("user", user)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("존재하지 않는 과제를 수정 시도하면 TaskNotExist 예외 발생")
    void modifyTask_NotFound() throws Exception {
        when(taskSubmissionService.editTaskSubmission(anyLong(), any(TaskSubmissionEditForm.class), any(), anyLong()))
                .thenThrow(new TaskNotExist());

        mockMvc.perform(put("/groups/1/submission/999")
                        .param("content", "Updated content")
                        .sessionAttr("user", user))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("과제 제출 주인만 수정 가능")
    void modifyTask_Forbidden() throws Exception {
        when(taskSubmissionService.editTaskSubmission(anyLong(), any(TaskSubmissionEditForm.class), any(), anyLong()))
                .thenThrow(new ForbiddenAccess(UNAUTHORIZED_SUBMISSION_ACCESS));

        mockMvc.perform(put("/groups/1/submission/1")
                        .param("content", "Updated content")
                        .sessionAttr("user", user))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("submission 삭제 테스트")
    void testSuccessDeleteTask() throws Exception {
        doNothing().when(taskSubmissionService).deleteTask(anyLong(), anyLong());

        mockMvc.perform(delete("/groups/1/submission/1")
                        .sessionAttr("user", user))
                .andExpect(status().isNoContent());
    }
}

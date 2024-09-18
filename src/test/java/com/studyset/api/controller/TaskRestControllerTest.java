package com.studyset.api.controller;

import com.studyset.exception.hanlder.RestExceptionHandler;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.service.TaskSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@SpringBootTest
@AutoConfigureMockMvc
class TaskRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskSubmissionService taskSubmissionService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TaskSubmissionRestController(taskSubmissionService))
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Submission 수정 성공 테스트")
    void testSuccessEditSchedule() throws Exception {
        TaskSubmissionDto taskSubmissionDto = TaskSubmissionDto.builder()
                .id(1L)
                .taskId(2L)
                .userId(3L)
                .build();

        MockMultipartFile file = new MockMultipartFile("file", "edit.pdf", "application/pdf", "Test file content".getBytes());
        MockMultipartFile taskId = new MockMultipartFile("taskId", "", "text/plain", "2".getBytes());
        MockMultipartFile userId = new MockMultipartFile("userId", "", "text/plain", "3".getBytes());
        MockMultipartFile content = new MockMultipartFile("content", "", "text/plain", "Edit contents".getBytes());

        when(taskSubmissionService.editTask(any())).thenReturn(taskSubmissionDto);

        mockMvc.perform(multipart("/groups/task/modifyTask")
                        .file(file)
                        .param("taskId", "2")
                        .param("userId", "3")
                        .param("content", "Edit contents")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }
    
    @Test
    @DisplayName("submission 삭제 테스트")
    void testSuccessDeleteTask() throws Exception {
        mockMvc.perform(delete("/groups/task/2/3/deleteTask"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}

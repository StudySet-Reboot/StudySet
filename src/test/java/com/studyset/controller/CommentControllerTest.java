package com.studyset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyset.domain.Group;
import com.studyset.domain.Task;
import com.studyset.domain.TaskSubmission;
import com.studyset.domain.User;
import com.studyset.domain.enumerate.GroupCategory;
import com.studyset.dto.task.CommentDto;
import com.studyset.exception.hanlder.RestExceptionHandler;
import com.studyset.repository.TaskSubmissionRepository;
import com.studyset.repository.UserRepository;
import com.studyset.service.CommentService;
import com.studyset.web.form.CommentForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CommentService commentService;
    @MockBean
    private TaskSubmissionRepository taskSubmissionRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CommentController(commentService, userRepository))
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("댓글 작성")
    public void addComment() throws Exception {
        User user = new User(9L, "John Doe", "aa@gmail.com", "google", "");
        Group group = new Group(9L, "Spring", GroupCategory.DESIGN, "aa", "123456");
        Task task = new Task(9L, group, "task", LocalDate.of(2023, 9, 16), LocalDate.of(2023, 9, 16), "");
        TaskSubmission taskSubmission = new TaskSubmission(9L, task, user, "dd", "");

        CommentForm form = new CommentForm();
        form.setUserId(9L);
        form.setSubmission_id(9L);
        form.setAnonymous(false);
        form.setContents("test");

        given(userRepository.findById(9L)).willReturn(Optional.of(user));
        given(taskSubmissionRepository.findById(9L)).willReturn(Optional.of(taskSubmission));

        CommentDto mockCommentDto = CommentDto.builder()
                        .id(9L)
                        .user_id(user.getId())
                        .submission_id(taskSubmission.getId())
                        .contents("This is test Comment")
                        .anonymous(false)
                        .updatedDate("20240916")
                        .build();

        given(commentService.addComment(form)).willReturn(mockCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/groups/userTask/addComment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.newComment.contents").value("This is test Comment"));
    }

    @Test
    @DisplayName("댓글 삭제")
    public void deleteComment() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/groups/userTask/{commentId}", commentId))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(commentId);
    }
}

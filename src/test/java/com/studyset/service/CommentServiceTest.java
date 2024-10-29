package com.studyset.service;

import com.studyset.domain.Comment;
import com.studyset.domain.Task;
import com.studyset.domain.TaskSubmission;
import com.studyset.domain.User;
import com.studyset.dto.task.CommentDto;
import com.studyset.repository.CommentRepository;
import com.studyset.repository.TaskSubmissionRepository;
import com.studyset.repository.UserRepository;
import com.studyset.web.form.CommentForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskSubmissionRepository taskSubmissionRepository;
    @Mock
    private CommentRepository commentRepository;

    private Task task;
    private User user;
    private TaskSubmission taskSubmission;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setEndTime(LocalDate.now().plusDays(1));
        ReflectionTestUtils.setField(task, "updatedDate", LocalDateTime.now());

        user = new User();
        user.setId(1L);

        taskSubmission = new TaskSubmission();
        taskSubmission.setId(1L);
        taskSubmission.setTask(task);
        taskSubmission.setUser(user);
        ReflectionTestUtils.setField(taskSubmission, "updatedDate", LocalDateTime.now());
        assertNotNull(ReflectionTestUtils.getField(taskSubmission, "updatedDate"));
    }

    @Test
    @DisplayName("댓글 작성")
    void addComment() {
        CommentForm commentForm = new CommentForm();
        commentForm.setUserId(1L);
        commentForm.setSubmission_id(1L);
        commentForm.setContents("댓글");

        //given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskSubmissionRepository.findById(1L)).thenReturn(Optional.of(taskSubmission));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedComment, "updatedDate", LocalDateTime.now());
            return savedComment;
        });

        //when
        CommentDto result = commentService.addComment(commentForm);

        //then
        assertNotNull(result);
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        assertEquals(formattedDate, result.getUpdatedDate());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제")
    void removeComment() {
        //given
        Long commentId = 1L;
        //when
        commentService.deleteComment(commentId);
        //then
        verify(commentRepository, times(1)).deleteById(commentId);
    }
}

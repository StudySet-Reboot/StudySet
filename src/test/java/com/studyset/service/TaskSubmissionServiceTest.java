package com.studyset.service;

import com.studyset.domain.Task;
import com.studyset.domain.TaskSubmission;
import com.studyset.domain.User;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.exception.TaskDeadlineException;
import com.studyset.repository.CommentRepository;
import com.studyset.repository.TaskRepository;
import com.studyset.repository.TaskSubmissionRepository;
import com.studyset.repository.UserRepository;
import com.studyset.web.form.TaskSubmissionEditForm;
import com.studyset.web.form.TaskSubmissionForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskSubmissionServiceTest {

    @Mock private TaskRepository taskRepository;

    @Mock private UserRepository userRepository;

    @Mock private TaskSubmissionRepository taskSubmissionRepository;

    @Mock private CommentRepository commentRepository;

    @Mock private MultipartFile file;

    @InjectMocks private TaskSubmissionService taskSubmissionService;

    private Task task;
    private User user;
    private TaskSubmission taskSubmission;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setEndTime(LocalDate.now().plusDays(1));

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
    @DisplayName("과제 제출에 성공")
    void addTaskSubmit_ShouldAddTaskSubmission() {
        //given
        TaskSubmissionForm form = new TaskSubmissionForm();
        form.setTaskId(1L);
        form.setUserId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskSubmissionRepository.save(any(TaskSubmission.class))).thenReturn(taskSubmission);

        //when
        TaskSubmissionDto result = taskSubmissionService.addTaskSubmit(form);
        //then
        assertNotNull(result);
        verify(taskSubmissionRepository, times(1)).save(any(TaskSubmission.class));
    }


    @Test
    @DisplayName("마감일이 지난 과제물은 제출 불가")
    void checkTaskDate_ShouldThrowTaskDeadlineException_WhenPastDeadline() {
        //given
        task.setEndTime(LocalDate.now().minusDays(1));
        TaskSubmissionForm form = new TaskSubmissionForm();
        form.setTaskId(1L);
        form.setUserId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        //when & then
        assertThrows(TaskDeadlineException.class, () -> taskSubmissionService.addTaskSubmit(form));
    }

    @Test
    @DisplayName("과제 제출물 수정에 성공")
    void editTaskSubmission_ShouldEditTaskSubmission() {
        //given
        when(taskSubmissionRepository.findById(1L)).thenReturn(Optional.of(taskSubmission));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(file.isEmpty()).thenReturn(true);
        TaskSubmissionEditForm taskSubmissionEditForm = new TaskSubmissionEditForm();
        taskSubmissionEditForm.setContent("수정");

        //when
        TaskSubmissionDto result = taskSubmissionService.editTaskSubmission(1L, taskSubmissionEditForm, file, 1L);

        //then
        assertNotNull(result);
        assertEquals(taskSubmission.getId(), result.getId());
        assertEquals(taskSubmissionEditForm.getContent(), result.getContents());
    }

    @Test
    @DisplayName("과제 제출 내역 삭제 성공")
    void deleteTask_ShouldDeleteTaskSubmission() {
        when(taskSubmissionRepository.findById(1L)).thenReturn(Optional.of(taskSubmission));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        taskSubmissionService.deleteTask(1L, 1L);

        verify(taskSubmissionRepository, times(1)).deleteById(1L);
    }



}
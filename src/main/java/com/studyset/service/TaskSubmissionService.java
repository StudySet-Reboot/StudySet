package com.studyset.service;

import com.studyset.api.exception.TaskNotExist;
import com.studyset.api.exception.UserNotExist;
import com.studyset.domain.Task;
import com.studyset.domain.TaskSubmission;
import com.studyset.domain.User;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.repository.TaskRepository;
import com.studyset.repository.TaskSubmissionRepository;
import com.studyset.repository.UserRepository;
import com.studyset.web.form.TaskSubmissionForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskSubmissionService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskSubmissionRepository taskSubmissionRepository;

    // 과제 제출
    @Transactional
    public TaskSubmissionDto addTaskSubmit(TaskSubmissionForm taskSubmissionForm) {
        Task task = taskRepository.findById(taskSubmissionForm.getTaskId())
                .orElseThrow(() -> new TaskNotExist());

        User user = userRepository.findById(taskSubmissionForm.getUserId())
                .orElseThrow(() -> new UserNotExist());

        TaskSubmission taskSubmission = new TaskSubmission();
        taskSubmission.setTask(task);
        taskSubmission.setUser(user);
        taskSubmission.setContents(taskSubmissionForm.getContent());
        taskSubmission.setFilePath(taskSubmissionForm.getFilePath());

        taskSubmissionRepository.save(taskSubmission);
        return taskSubmission.toDto();
    }

    // 그룹원의 과제 제출 목록 조회
    public List<TaskSubmissionDto> getTaskSubmissionById(Long taskId) {
        List<TaskSubmission> taskSubmissionList = taskSubmissionRepository.findByTaskId(taskId);
        return taskSubmissionList.stream().map(TaskSubmission::toDto).collect(Collectors.toList());
    }

    // 과제ID로 과제 조회
    public TaskSubmissionDto getTaskSubmission (Long taskId, Long userId) {
        TaskSubmission taskSubmission = taskSubmissionRepository.findByTaskIdAndUserId(taskId, userId);
        if (taskSubmission == null) {
            return null;
        }
        return taskSubmission.toDto();
    }

    // 과제 제출 ID로 제출한 과제 조회
    public TaskSubmissionDto findTaskSubmission(Long taskSubmissionId) {
        Optional<TaskSubmission> taskSubmission = taskSubmissionRepository.findById(taskSubmissionId);

        return taskSubmission
                .map(TaskSubmission::toDto)
                .orElseThrow(TaskNotExist::new);

    }
}

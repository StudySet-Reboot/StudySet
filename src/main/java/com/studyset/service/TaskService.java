package com.studyset.service;

import com.studyset.api.exception.GroupNotExist;
import com.studyset.api.exception.InvalidEndDateException;
import com.studyset.api.exception.TaskNotExist;
import com.studyset.domain.Group;
import com.studyset.domain.Task;
import com.studyset.dto.task.TaskDto;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.TaskRepository;
import com.studyset.web.form.TaskCreateForm;
import com.studyset.web.form.TaskEditForm;
import com.studyset.web.form.TaskSubmissionEditForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final GroupRepository groupRepository;

    // 과제 조회
    public List<TaskDto> getTaskByGroupId(Long groupId) {
        List<Task> taskList = taskRepository.findByGroupId(groupId);
        return taskList.stream().map(Task::toDto).collect(Collectors.toList());
    }

    // 과제 상세 조회
    public TaskDto getTaskDetailByTaskId(Long taskId) {
       Task task = taskRepository.findById(taskId)
               .orElseThrow(() -> new TaskNotExist());
        return task.toDto();
    }

    // 과제 생성
    @Transactional
    public TaskDto addTask(TaskCreateForm taskForm) {
        Group group = groupRepository.findGroupById(taskForm.getGroupId())
                .orElseThrow(() -> new GroupNotExist());

        if (taskForm.getEndTime() != null &&
            taskForm.getStartTime().isAfter(taskForm.getEndTime())) {
            throw new InvalidEndDateException();
        }

        Task task = new Task();
        task.setTaskName(taskForm.getTaskName());
        task.setGroup(group);
        task.setDescription(taskForm.getDescription());
        task.setStartTime(taskForm.getStartTime());
        task.setEndTime(taskForm.getEndTime());

        taskRepository.save(task);
        return task.toDto();
    }

    // 과제 수정
    @Transactional
    public TaskDto editTask(TaskEditForm taskForm) {
        // 기존 과제 정보 조회
        Optional<Task> taskOptional = taskRepository.findById(taskForm.getId());
        if (taskOptional.isPresent()) {
            // 기존 제출물 업데이트
            Task existingTask = taskOptional.get();

            existingTask.setTaskName(taskForm.getTaskName());
            existingTask.setStartTime(taskForm.getStartTime());
            existingTask.setEndTime(taskForm.getEndTime());
            existingTask.setDescription(taskForm.getDescription());

            taskRepository.save(existingTask);
            return existingTask.toDto();
        } else {
            throw new TaskNotExist();
        }
    }

}

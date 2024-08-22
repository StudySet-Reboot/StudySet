package com.studyset.service;

import com.studyset.api.exception.GroupNotExist;
import com.studyset.domain.Group;
import com.studyset.domain.Task;
import com.studyset.dto.task.TaskDto;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.TaskRepository;
import com.studyset.web.form.TaskCreateForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // 과제 생성
    @Transactional
    public TaskDto addTask(TaskCreateForm taskForm) {
        Group group = groupRepository.findById(taskForm.getGroupId())
                .orElseThrow(() -> new GroupNotExist());

        Task task = new Task();
        task.setTaskName(taskForm.getTaskName());
        task.setGroup(group);
        task.setDescription(taskForm.getDescription());
        task.setStartTime(taskForm.getStartTime());
        task.setEndTime(taskForm.getEndTime());

        taskRepository.save(task);
        return task.toDto();
    }
}

package com.studyset.service;

import com.studyset.exception.GroupNotExist;
import com.studyset.exception.InvalidEndDate;
import com.studyset.exception.TaskNotExist;
import com.studyset.domain.Group;
import com.studyset.domain.Task;
import com.studyset.dto.task.TaskDto;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.TaskRepository;
import com.studyset.web.form.TaskCreateForm;
import com.studyset.web.form.TaskEditForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final GroupRepository groupRepository;

    /**
     * 그룹의 모든 과제를 조회합니다.
     *
     * @param groupId 그룹 ID
     * @return List<TaskDto> 해당 그룹의 모든 과제 목록
     */
    public List<TaskDto> getTaskByGroupId(Long groupId) {
        List<Task> taskList = taskRepository.findByGroupId(groupId);
        return taskList.stream().map(Task::toDto).collect(Collectors.toList());
    }

    /**
     * 현재 날짜가 포함된 과제를 조회합니다.
     *
     * @param groupId 그룹 ID
     * @return List<TaskDto> 현재 날짜 포함 과제 목록
     */
    public List<TaskDto> getCurrentTasksByGroupId(Long groupId) {
        LocalDate currentDate = LocalDate.now();
        List<Task> taskList = taskRepository.findCurrentTasksByGroupId(groupId, currentDate);

        return taskList.stream()
                .map(Task::toDto)
                .sorted(Comparator.comparing(TaskDto::getEndTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * 과제의 상세 정보를 조회합니다.
     *
     * @param taskId 과제 ID
     * @return TaskDto 해당 과제의 상세 정보
     * @throws TaskNotExist 해당 과제가 존재하지 않을 경우
     */
    public TaskDto getTaskDetailByTaskId(Long taskId) {
       Task task = taskRepository.findById(taskId)
               .orElseThrow(() -> new TaskNotExist());
        return task.toDto();
    }

    /**
     * 새로운 과제를 생성합니다.
     *
     * @param taskForm 과제 생성 form 객체
     * @return TaskDto 생성된 과제 정보
     * @throws GroupNotExist 해당 그룹이 존재하지 않을 경우
     * @throws InvalidEndDate 종료 시간이 시작 시간보다 이전일 경우
     */
    @Transactional
    public TaskDto addTask(TaskCreateForm taskForm) {
        Group group = groupRepository.findGroupById(taskForm.getGroupId())
                .orElseThrow(() -> new GroupNotExist());

        if (taskForm.getEndTime() != null &&
            taskForm.getStartTime().isAfter(taskForm.getEndTime())) {
            throw new InvalidEndDate();
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

    /**
     * 기존 과제를 수정합니다.
     *
     * @param taskForm 과제 수정 폼
     * @return TaskDto 수정된 과제 정보
     * @throws TaskNotExist 해당 과제가 존재하지 않을 경우
     */
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

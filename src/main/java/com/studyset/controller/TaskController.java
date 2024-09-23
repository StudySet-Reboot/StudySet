package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.dto.user.UserDto;
import com.studyset.service.*;
import com.studyset.web.form.TaskCreateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller()
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;
    private final JoinService joinService;
    private final TaskSubmissionService taskSubmissionService;

    // 과제 메인페이지 이동
    @GetMapping("/{groupId}/task")
    public String taskMain(@SessionAttribute("group") GroupDto group, Model model) {
        List<TaskDto> taskList = taskService.getTaskByGroupId(group.getId());

        // endTime을 기준으로 정렬
        taskList.sort(Comparator.comparing(TaskDto::getEndTime, Comparator.nullsLast(Comparator.naturalOrder())));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (TaskDto task : taskList) {
            if (task.getStartTime() != null) {
                task.setStartTimeFormatted(task.getStartTime().format(formatter));
            }
            if (task.getEndTime() != null) {
                task.setEndTimeFormatted(task.getEndTime().format(formatter));
            }
        }
        model.addAttribute("group", group);
        model.addAttribute("taskList", taskList);

        return "/thyme/task/taskMain";
    }

    // 과제 생성
    @PostMapping("/task/create")
    public ResponseEntity<Map<String, Object>> addTask(@ModelAttribute TaskCreateForm task) {
        TaskDto newTask = taskService.addTask(task);

        Map<String, Object> response = new HashMap<>();
        response.put("newTask", newTask);
        return ResponseEntity.ok(response);
    }

    // 과제 상세페이지 이동
    @GetMapping("/{groupId}/{taskId}/taskDetail")
    public String taskDetail(@PathVariable Long taskId, @SessionAttribute("group") GroupDto group, @SessionAttribute("user") User user, Model model) {
        List<UserDto> userList = joinService.getUserByGroupId(group.getId());
        TaskDto task = taskService.getTaskDetailByTaskId(taskId);
        List<TaskSubmissionDto> taskSubmissionList = taskSubmissionService.getTaskSubmissionById(taskId);

        // 사용자 ID와 제출 정보를 매핑하기 위한 맵 생성
        Map<Long, TaskSubmissionDto> userSubmissionMap = taskSubmissionList.stream()
                .collect(Collectors.toMap(ts -> ts.getUserId(), ts -> ts));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (task.getEndTime() != null) {
            task.setEndTimeFormatted(task.getEndTime().format(formatter));
        }

        model.addAttribute("group", group);
        model.addAttribute("userList", userList);
        model.addAttribute("loginUser", user);
        model.addAttribute("task", task);
        model.addAttribute("taskSubmissionMap", userSubmissionMap);
        return "/thyme/task/taskDetail";
    }
}
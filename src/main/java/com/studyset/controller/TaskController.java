package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.dto.user.UserDto;
import com.studyset.service.JoinService;
import com.studyset.service.TaskService;
import com.studyset.web.form.TaskCreateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller()
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;
    private final JoinService joinService;

    // 과제 메인페이지 이동
    @GetMapping("/{groupId}/task")
    public String taskMain(@SessionAttribute("group") GroupDto group, Model model) {
        List<TaskDto> taskList = taskService.getTaskByGroupId(group.getId());

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (task.getEndTime() != null) {
            task.setEndTimeFormatted(task.getEndTime().format(formatter));
        }

        model.addAttribute("group", group);
        model.addAttribute("userList", userList);
        model.addAttribute("loginUser", user);
        model.addAttribute("task", task);
        return "/thyme/task/taskDetail";
    }

    // 유저별 과제페이지 이동(=과제 조회)
    @GetMapping("/{taskId}/{userId}/userTask")
    public String userTask(@PathVariable Long taskId, @PathVariable Long userId, Model model) {
        // 추후

        return "/thyme/task/userTask";
    }

    // 과제 제출페이지 이동
    @GetMapping("/{groupId}/{taskId}/submitTask")
    public String taskSubmit(@PathVariable Long taskId, @SessionAttribute("user") User user, @SessionAttribute("group") GroupDto group, Model model) {
        TaskDto task = taskService.getTaskDetailByTaskId(taskId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (task.getEndTime() != null) {
            task.setEndTimeFormatted(task.getEndTime().format(formatter));
        }

        model.addAttribute("group", group);
        model.addAttribute("task", task);
        model.addAttribute("user", user);
        return "/thyme/task/userTaskSubmit";
    }

    // 과제 제출
    @PostMapping("/task/submitTask")
    public ResponseEntity<Map<String, Object>> submitTask() {

        return null;
    }
}
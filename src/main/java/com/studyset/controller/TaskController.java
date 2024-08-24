package com.studyset.controller;

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

    // 과제 메인 이동
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

    // 과제 상세 이동
    @GetMapping("/{groupId}/{taskId}/taskDetail")
    public String taskDetail(@PathVariable Long taskId, @SessionAttribute("group") GroupDto group, Model model) {
        System.out.println("과제 상세 이동 컨트롤러 진입");
        List<UserDto> userList = joinService.getUserByGroupId(group.getId());
        System.out.println("그룹 존재 에러");
        for (UserDto user : userList) {
            System.out.println(user.getId());
        }
        TaskDto task = taskService.getTaskDetailByTaskId(taskId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (task.getEndTime() != null) {
            task.setEndTimeFormatted(task.getEndTime().format(formatter));
        }

        model.addAttribute("group", group);
        model.addAttribute("userList", userList);
        model.addAttribute("task", task);
        return "/thyme/task/taskDetail";
    }

    // 유저별 과제 이동
    @GetMapping("/{taskId}/{userId}/userTask")
    public String userTask(@PathVariable Long taskId, @PathVariable Long userId, @SessionAttribute("group") GroupDto group, Model model) {
        // 추후

        return "/thyme/task/userTask";
    }
}
package com.studyset.controller;

import com.studyset.api.exception.TaskNotExist;
import com.studyset.domain.TaskSubmission;
import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.dto.user.UserDto;
import com.studyset.repository.TaskSubmissionRepository;
import com.studyset.service.JoinService;
import com.studyset.service.TaskService;
import com.studyset.service.TaskSubmissionService;
import com.studyset.web.form.TaskCreateForm;
import com.studyset.web.form.TaskSubmissionForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.nio.file.StandardCopyOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
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
    private final TaskSubmissionRepository taskSubmissionRepository;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

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
    public RedirectView submitTask(@ModelAttribute TaskSubmissionForm taskSubmit,
                                   @SessionAttribute("group") GroupDto group,
                                   @RequestParam("file") MultipartFile file) {
        String redirectUrl = "/groups/" + group.getId() + "/" + taskSubmit.getTaskId() + "/taskDetail";
        String filePath = saveFile(file);
        taskSubmit.setFilePath(filePath);

        TaskSubmissionDto newTaskSubmit = taskSubmissionService.addTaskSubmit(taskSubmit);

        Map<String, Object> response = new HashMap<>();
        response.put("newTaskSubmit", newTaskSubmit);
        return new RedirectView(redirectUrl);
    }

    // 파일 제출
    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }

            Path targetLocation = fileStorageLocation.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), ex);
        }
    }

    // 유저별 과제페이지 이동(=과제 조회)
    @GetMapping("/{groupId}/{taskId}/{userId}/userTask")
    public String userTask(@PathVariable Long taskId, @PathVariable Long userId,
                           @SessionAttribute("user") User user,
                           @SessionAttribute("group") GroupDto group,
                           Model model) {
        TaskDto taskDto = taskService.getTaskDetailByTaskId(taskId);
        TaskSubmissionDto taskSubmissionDto = taskSubmissionService.getTaskSubmission(taskId, userId);

        model.addAttribute("task", taskSubmissionDto);
        model.addAttribute("taskDto", taskDto);
        model.addAttribute("user", user);
        model.addAttribute("group", group);
        return "/thyme/task/userTask";
    }

    // 파일 다운로드
    @GetMapping("/download/{taskSubmissionId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long taskSubmissionId) {
        // taskSubmissionId로 TaskSubmission 조회
        TaskSubmissionDto taskSubmissionDto = taskSubmissionRepository.findById(taskSubmissionId)
                .orElseThrow(() -> new TaskNotExist()).toDto();

        // 저장된 filePath 가져오기
        String filePath = taskSubmissionDto.getFilePath();
        Path fileLocation = Paths.get(filePath).normalize();

        try {
            Resource resource = new UrlResource(fileLocation.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }
    }
}
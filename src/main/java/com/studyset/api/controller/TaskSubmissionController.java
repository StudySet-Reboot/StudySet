package com.studyset.api.controller;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.service.TaskService;
import com.studyset.service.TaskSubmissionService;
import com.studyset.web.form.TaskEditForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class TaskSubmissionController {
    private final TaskSubmissionService taskSubmissionService;
    private final TaskService taskService;

    // 과제 수정 페이지 이동
    @GetMapping("/{groupId}/{taskId}/modifyTask")
    public String modifyTask(@PathVariable Long taskId, @SessionAttribute("user") User user, @SessionAttribute("group") GroupDto group, Model model) {
        TaskSubmissionDto taskSubmissionDto = taskSubmissionService.getTaskSubmission(taskId, user.getId());
        TaskDto taskDto = taskService.getTaskDetailByTaskId(taskId);

        // 유저의 제출물에 대한 정보
        model.addAttribute("task", taskSubmissionDto);
        // 과제에 대한 정보
        model.addAttribute("taskDto", taskDto);
        model.addAttribute("user", user);
        model.addAttribute("group", group);

        return "/thyme/task/modifyUserTaskSubmit";
    }

    // 과제 수정
    @PutMapping("/task/modifyTask")
    public ResponseEntity<?> modifyTask(@SessionAttribute("Group") Group group,
                                        @ModelAttribute TaskEditForm taskEditForm,
                                        @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // 과제 수정 로직
            // taskService.updateTask(taskId, userId, content, file);

            // 수정 성공 후, 리다이렉션 URL을 포함하여 응답 반환
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "groupId", group.getId(),
                    "taskId", taskEditForm.getTaskId(),
                    "userId", taskEditForm.getUserId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "errorMessage", e.getMessage()));
        }
    }

    // 과제 삭제

}

package com.studyset.api.controller;

import com.studyset.dto.task.TaskDto;
import com.studyset.service.TaskService;
import com.studyset.web.form.TaskEditForm;
import com.studyset.web.form.TaskSubmissionEditForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class TaskRestController {
    private final TaskService taskService;

    // 과제(task) 수정
    @PutMapping("/task/modifyTask")
    public ResponseEntity<TaskDto> modifyTask(@ModelAttribute TaskEditForm taskEditForm) {
        log.info("과제 수정: {}", taskEditForm.getId());
        TaskDto taskDto = taskService.editTask(taskEditForm);
        return ResponseEntity.ok(taskDto);
    }
}

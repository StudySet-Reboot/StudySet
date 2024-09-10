package com.studyset.api.controller;

import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.service.TaskSubmissionService;
import com.studyset.web.form.TaskEditForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class RestTaskSubmissionController {
    private final TaskSubmissionService taskSubmissionService;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    // 과제 수정
    @PutMapping("/task/modifyTask")
    public ResponseEntity<TaskSubmissionDto> modifyTask(@ModelAttribute TaskEditForm taskEditForm,
                                                        @RequestParam(value = "file", required = false) MultipartFile file) {
        log.info("Editing task: {}", taskEditForm.getTaskId());

        // 파일이 업로드된 경우에만 파일 경로를 변경
        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file);
            taskEditForm.setFilePath(filePath);
        } else {
            // 업로드된 파일이 없는 경우 기존 파일 경로 유지
            TaskSubmissionDto dto = taskSubmissionService.getTaskSubmission(taskEditForm.getTaskId(), taskEditForm.getUserId());
            taskEditForm.setFilePath(dto.getFilePath());
        }
        TaskSubmissionDto taskSubmissionDto = taskSubmissionService.editTask(taskEditForm);
        return ResponseEntity.ok(taskSubmissionDto);
    }

    // 파일 제출
    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) { return null; }

        try {
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }

            String originalFilename = file.getOriginalFilename();
            // 중복된 파일 덮어쓰기 방지
            String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(originalFilename);
            Path targetLocation = fileStorageLocation.resolve(fileName);
            // 실질적인 파일명만 저장 (경로X)
            String fileNameOnly = targetLocation.getFileName().toString();

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileNameOnly;
        } catch (IOException ex) {
            throw new RuntimeException("파일을 저장할 수 없습니다. 다시 시도해 주세요!", ex);
        }
    }

    // 과제 삭제
    @DeleteMapping("/task/{taskId}/{userId}/deleteTask")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, @PathVariable Long userId) {
        log.info("Deleting task: {} user: {}", taskId, userId);
        taskSubmissionService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }
}

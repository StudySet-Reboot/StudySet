package com.studyset.api.controller;

import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.service.TaskSubmissionService;
import com.studyset.web.form.TaskSubmissionEditForm;
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

// TODO: URI 명명 규칙에 따라 수정 필요
// 현재 URI가 RESTful 규칙을 따르지 않고 있습니다.
// 예: /groups/{groupId}/tasks/{taskId}/submissions/{submissionId} 등으로 리팩토링 필요

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class TaskSubmissionRestController {

    private final TaskSubmissionService taskSubmissionService;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    /**
     * 제출한 과제를 수정합니다.
     *
     * @param taskSubmissionEditForm 수정한 과제 정보를 담은 TaskSubmissionEditForm 객체
     * @param file    (선택) 과제에 첨부할 파일
     * @return 그룹의 스캐줄 조정표 목록을 포함한 OK 응답
     */
    @PutMapping("/submission/modifySubmission")
    public ResponseEntity<TaskSubmissionDto> modifyTask(@ModelAttribute TaskSubmissionEditForm taskSubmissionEditForm,
                                                        @RequestParam(value = "file", required = false) MultipartFile file) {
        log.info("제출물 수정: {}", taskSubmissionEditForm.getTaskId());

        // 파일이 업로드된 경우에만 파일 경로를 변경
        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file);
            taskSubmissionEditForm.setFilePath(filePath);
        } else {
            // 업로드된 파일이 없는 경우 기존 파일 경로 유지
            TaskSubmissionDto dto = taskSubmissionService.getTaskSubmission(taskSubmissionEditForm.getTaskId(), taskSubmissionEditForm.getUserId());
            taskSubmissionEditForm.setFilePath(dto.getFilePath());
        }
        TaskSubmissionDto taskSubmissionDto = taskSubmissionService.editTask(taskSubmissionEditForm);
        return ResponseEntity.ok(taskSubmissionDto);
    }

    /**
     * 과제에 첨부한 파일을 저장합니다.
     *
     * @param file  저장할 파일
     * @return 저장된 파일 이름
     */
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

    /**
     * 제출한 과제를 삭제합니다.
     *
     * @param taskId 삭제할 Task ID
     * @param userId    과제를 제출한 User ID
     * @return 삭제 후 204(No Content) 응답
     */
    @DeleteMapping("/submission/{taskId}/{userId}/deleteSubmission")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, @PathVariable Long userId) {
        log.info("Deleting task: {} user: {}", taskId, userId);
        taskSubmissionService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }
}

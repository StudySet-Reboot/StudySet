package com.studyset.api.controller;

import com.studyset.domain.User;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.service.TaskSubmissionService;
import com.studyset.web.form.TaskSubmissionEditForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/groups/{groupId}")
@RequiredArgsConstructor
@Slf4j
public class TaskSubmissionRestController {

    private final TaskSubmissionService taskSubmissionService;

    /**
     * 제출한 과제를 수정합니다.
     *
     * @param taskSubmissionEditForm 수정한 과제 정보를 담은 TaskSubmissionEditForm 객체
     * @param file    (선택) 과제에 첨부할 파일
     * @return 그룹의 스캐줄 조정표 목록을 포함한 OK 응답
     */
    @PutMapping("/submission/{submissionId}")
    public ResponseEntity<TaskSubmissionDto> modifyTask(@PathVariable Long submissionId,
                                                        @ModelAttribute TaskSubmissionEditForm taskSubmissionEditForm,
                                                        @RequestParam(value = "file", required = false) MultipartFile file,
                                                        @SessionAttribute("user") User user) {
        log.info("제출물 수정: {}", submissionId);
        TaskSubmissionDto taskSubmissionDto = taskSubmissionService
                .editTaskSubmission(submissionId, taskSubmissionEditForm, file, user.getId());
        return ResponseEntity.ok(taskSubmissionDto);
    }

    /**
     * 제출한 과제를 삭제합니다.
     *
     * @param submissionId 삭제할 Task ID
     * @param user    현재 로그인한 user
     * @return 삭제 후 204(No Content) 응답
     */
    @DeleteMapping("/submission/{submissionId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long submissionId,
                                           @SessionAttribute("user") User user) {
        log.info("과제 제출 삭제: submission: {} user: {}", submissionId, user.getId());
        taskSubmissionService.deleteTask(submissionId, user.getId());
        return ResponseEntity.noContent().build();
    }
}

package com.studyset.service;

import com.studyset.domain.Task;
import com.studyset.domain.TaskSubmission;
import com.studyset.domain.User;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.exception.TaskDeadlineException;
import com.studyset.exception.TaskNotExist;
import com.studyset.exception.UserNotExist;
import com.studyset.repository.CommentRepository;
import com.studyset.repository.TaskRepository;
import com.studyset.repository.TaskSubmissionRepository;
import com.studyset.repository.UserRepository;
import com.studyset.web.form.TaskSubmissionEditForm;
import com.studyset.web.form.TaskSubmissionForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskSubmissionService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskSubmissionRepository taskSubmissionRepository;
    private final CommentRepository commentRepository;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    /**
     * 과제를 제출합니다.
     *
     * @param taskSubmissionForm 과제 제출 form 객체
     * @return 제출된 과제의 DTO
     * @throws UserNotExist 사용자가 존재하지 않을 경우
     */
    @Transactional
    public TaskSubmissionDto addTaskSubmit(TaskSubmissionForm taskSubmissionForm) {
        Task task = checkTaskDate(taskSubmissionForm.getTaskId());

        User user = userRepository.findById(taskSubmissionForm.getUserId())
                .orElseThrow(() -> new UserNotExist());

        TaskSubmission taskSubmission = new TaskSubmission();
        taskSubmission.setTask(task);
        taskSubmission.setUser(user);
        taskSubmission.setContents(taskSubmissionForm.getContent());
        taskSubmission.setFilePath(taskSubmissionForm.getFilePath());

        taskSubmissionRepository.save(taskSubmission);
        return taskSubmission.toDto();
    }

    /**
     * 과제를 수정합니다.
     *
     * @param taskSubmissionEditForm 과제 제출 수정 form 객체
     * @return 수정된 과제 제출의 DTO
     */
    @Transactional
    public TaskSubmissionDto editTask(TaskSubmissionEditForm taskSubmissionEditForm) {
        // 기존 제출물 조회
        TaskSubmission existingSubmission = taskSubmissionRepository.findByTaskIdAndUserId(taskSubmissionEditForm.getTaskId(), taskSubmissionEditForm.getUserId());

        if (existingSubmission != null) {
            checkTaskDate(taskSubmissionEditForm.getTaskId() );
            // 기존 제출물 업데이트
            existingSubmission.setContents(taskSubmissionEditForm.getContent());
            if (taskSubmissionEditForm.getFilePath() != null) {
                existingSubmission.setFilePath(taskSubmissionEditForm.getFilePath());
            }
            taskSubmissionRepository.save(existingSubmission);
        }
        return existingSubmission.toDto();
    }

    /**
     * 과제를 삭제합니다.
     *
     * @param taskId 과제 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        checkTaskDate(taskId);
        TaskSubmission existingSubmission = taskSubmissionRepository.findByTaskIdAndUserId(taskId, userId);
        Long taskSubmissionId = existingSubmission.toDto().getId();
        commentRepository.deleteBySubmissionId(taskSubmissionId);
        taskSubmissionRepository.deleteById(taskSubmissionId);
    }

    /**
     * id로 Task를 조회해 마감기한을 체크합니다.
     * @param taskId 과제 ID
     * @return 마감기한이 지나지 않은 Task
     * @throws TaskNotExist 과제가 존재하지 않을 경우
     * @throws TaskDeadlineException 과제의 마감기한이 지났을 경우
     */
    public Task checkTaskDate(Long taskId) {
        // 과제 기한 검증
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotExist());
        if (task.getEndTime().isBefore(LocalDate.now()) && !task.getEndTime().isEqual(LocalDate.now())) {
            throw new TaskDeadlineException();
        }
        return task;
    }

    /**
     * 특정 과제에 대한 그룹원의 제출 목록을 조회합니다.
     *
     * @param taskId 과제 ID
     * @return 과제 제출 목록의 DTO 리스트
     */
    public List<TaskSubmissionDto> getTaskSubmissionById(Long taskId) {
        List<TaskSubmission> taskSubmissionList = taskSubmissionRepository.findByTaskId(taskId);
        return taskSubmissionList.stream().map(TaskSubmission::toDto).collect(Collectors.toList());
    }

    /**
     * 과제 ID로 과제를 조회합니다.
     *
     * @param taskId 과제 ID
     * @param userId 사용자 ID
     * @return 과제 제출 DTO, 존재하지 않으면 null
     */
    public TaskSubmissionDto getTaskSubmission(Long taskId, Long userId) {
        TaskSubmission taskSubmission = taskSubmissionRepository.findByTaskIdAndUserId(taskId, userId);
        if (taskSubmission == null) {
            return null;
        }
        return taskSubmission.toDto();
    }

    /**
     * 과제 제출 ID로 제출한 과제를 조회합니다.
     *
     * @param taskSubmissionId 과제 제출 ID
     * @return 제출한 과제 DTO
     * @throws TaskNotExist 과제가 존재하지 않을 경우
     */
    public TaskSubmissionDto findTaskSubmission(Long taskSubmissionId) {
        Optional<TaskSubmission> taskSubmission = taskSubmissionRepository.findById(taskSubmissionId);

        return taskSubmission
                .map(TaskSubmission::toDto)
                .orElseThrow(TaskNotExist::new);

    }

    /**
     * 파일을 저장합니다.
     *
     * @param file 업로드할 파일
     * @return 저장된 파일의 이름
     */
    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

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
     * 파일을 다운로드합니다.
     *
     * @param filePath 다운로드할 파일 경로
     * @return 파일 다운로드 응답
     */
    public ResponseEntity<?> downloadFile(String filePath) {
        Path fileLocation = fileStorageLocation.resolve(filePath).normalize();

        try {
            Resource resource = new UrlResource(fileLocation.toUri());

            if (resource.exists() && resource.isReadable()) {
                String fileName = fileLocation.getFileName().toString(); // 파일명 가져오기
                String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()); // URL 인코딩
                encodedFileName = encodedFileName.replace("+", "%20");

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(Files.probeContentType(fileLocation)))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file path");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File reading error");
        }
    }
}

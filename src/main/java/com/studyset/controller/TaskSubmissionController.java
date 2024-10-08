package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.task.CommentDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.service.*;
import com.studyset.web.form.TaskSubmissionForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller()
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class TaskSubmissionController {

    private final TaskService taskService;
    private final TaskSubmissionService taskSubmissionService;
    private final UserService userService;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    private final CommentService commentService;

    // 과제 제출페이지 이동
    @GetMapping("/{groupId}/{taskId}/submitTask")
    public String taskSubmit(@PathVariable Long taskId,
                             @SessionAttribute("user") User user,
                             @SessionAttribute("group") GroupDto group,
                             Model model) {
        // 과제 제출 페이지에 들어오기 전, 과제를 이미 제출했는 지 검증 -> 이미 제출했으면 수정 페이지로 이동
        TaskSubmissionDto taskSubmissionDto = taskSubmissionService.getTaskSubmission(taskId, user.getId());

        if (taskSubmissionDto == null) {
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
        // 과제를 이미 제출한 적 있으면 과제 제출 페이지가 아닌 과제 수정 페이지로 이동
        else {
            TaskDto taskDto = taskService.getTaskDetailByTaskId(taskId);

            // 유저의 제출물에 대한 정보
            model.addAttribute("task", taskSubmissionDto);
            // 과제에 대한 정보
            model.addAttribute("taskDto", taskDto);
            model.addAttribute("user", user);
            model.addAttribute("group", group);

            return "/thyme/task/modifyUserTaskSubmit";
        }
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

    // 유저별 과제페이지 이동(=과제 조회)
    @GetMapping("/{groupId}/{taskId}/{userId}/userTask")
    public String userTask(@PathVariable Long taskId, @PathVariable Long userId,
                           @SessionAttribute("user") User user,
                           @SessionAttribute("group") GroupDto group,
                           Model model) {
        TaskDto taskDto = taskService.getTaskDetailByTaskId(taskId);
        TaskSubmissionDto taskSubmissionDto = taskSubmissionService.getTaskSubmission(taskId, userId);
        List<CommentDto> commentList = commentService.getCommentBySubmissionId(taskSubmissionDto.getId());
        // 댓글 작성자 userId로 User 정보 조회
        List<Long> userIds = commentList.stream()
                .map(CommentDto::getUser_id)
                .collect(Collectors.toList());
        Map<Long, User> usersMap = userService.findUsersByIds(userIds);  // userId로 User 정보 조회

        model.addAttribute("task", taskSubmissionDto);  // 특정 과제에 대한 제출물
        model.addAttribute("taskDto", taskDto);         // 특정 과제
        model.addAttribute("commentList", commentList); // 댓글 리스트
        model.addAttribute("usersMap", usersMap);   // 댓글 작성자 리스트
        model.addAttribute("user", user);   // 현재 로그인한 유저
        model.addAttribute("group", group);
        return "/thyme/task/userTask";
    }

    // 과제 수정 페이지 이동
    @GetMapping("/{groupId}/{taskId}/modifyTask")
    public String showModifyTask(@PathVariable Long taskId,
                                 @SessionAttribute("user") User user,
                                 @SessionAttribute("group") GroupDto group,
                                 Model model) {
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

    // 파일 다운로드
    @GetMapping("/download/{taskSubmissionId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long taskSubmissionId) {
        // taskSubmissionId로 TaskSubmission 조회
        TaskSubmissionDto taskSubmissionDto = taskSubmissionService.findTaskSubmission(taskSubmissionId);

        String filePath = taskSubmissionDto.getFilePath();
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

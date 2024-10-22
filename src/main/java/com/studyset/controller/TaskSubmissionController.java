package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.task.CommentDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.dto.task.TaskSubmissionDto;
import com.studyset.service.CommentService;
import com.studyset.service.TaskService;
import com.studyset.service.TaskSubmissionService;
import com.studyset.service.UserService;
import com.studyset.web.form.TaskSubmissionForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class TaskSubmissionController {

    private final TaskService taskService;
    private final TaskSubmissionService taskSubmissionService;
    private final UserService userService;
    private final CommentService commentService;

    /**
     * 과제 제출/수정 페이지로 이동합니다.
     *
     * @param taskId 제출할 과제의 ID
     * @param user 현재 로그인한 유저
     * @param group 해당 과제가 등록된 그룹
     * @param model 뷰에 전달할 데이터 객체
     * @return 반환할 뷰 경로
     */
    @GetMapping("/{groupId}/tasks/{taskId}/submit-modify")
    public String taskSubmit(@PathVariable Long taskId,
                             @SessionAttribute("user") User user,
                             @SessionAttribute("group") GroupDto group,
                             Model model) {
        TaskSubmissionDto taskSubmissionDto = taskSubmissionService.getTaskSubmission(taskId, user.getId());
        TaskDto taskDto = taskService.getTaskDetailByTaskId(taskId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (taskDto.getEndTime() != null) {
            taskDto.setEndTimeFormatted(taskDto.getEndTime().format(formatter));
        }

        model.addAttribute("task", taskSubmissionDto);
        model.addAttribute("taskDto", taskDto);
        model.addAttribute("user", user);
        model.addAttribute("group", group);

        // 과제 제출 페이지에 들어오기 전, 과제를 이미 제출했는 지 검증 -> 이미 제출했으면 수정 페이지로 이동
        return taskSubmissionDto == null ?
                "/thyme/task/userTaskSubmit" : "/thyme/task/modifyUserTaskSubmit";
    }

    /**
     * 과제를 제출합니다.
     *
     * @param taskSubmit 제출물 정보가 담긴 폼
     * @param group 해당 과제가 등록된 그룹
     * @param file 제출 시 첨부된 파일
     * @return 특정 뷰로 반환
     */
    @PostMapping("/task/submit")
    public RedirectView submitTask(@ModelAttribute TaskSubmissionForm taskSubmit,
                                   @SessionAttribute("group") GroupDto group,
                                   @RequestParam("file") MultipartFile file) {
        String redirectUrl = "/groups/" + group.getId() + "/" + taskSubmit.getTaskId() + "/task-detail";
        String filePath = taskSubmissionService.saveFile(file);
        taskSubmit.setFilePath(filePath);

        TaskSubmissionDto newTaskSubmit = taskSubmissionService.addTaskSubmit(taskSubmit);

        Map<String, Object> response = new HashMap<>();
        response.put("newTaskSubmit", newTaskSubmit);
        return new RedirectView(redirectUrl);
    }

    /**
     * 과제 페이지로 이동하여 그룹원이 제출한 과제를 조회합니다.
     *
     * @param taskId 제출할 과제의 ID
     * @param userId 선택한 그룹원의 ID
     * @param user 현재 로그인한 유저
     * @param group 해당 과제가 등록된 그룹
     * @param model 뷰에 전달할 데이터 객체
     * @return 반환할 뷰 경로
     */
    @GetMapping("/{groupId}/tasks/{taskId}/{userId}/usertask")
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

    /**
     * 제출된 파일을 다운로드 합니다.
     *
     * @param taskSubmissionId 제출물 ID
     * @return 응답 상태와 데이터를 포함한 Map 객체
     */
    @GetMapping("/download/{taskSubmissionId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long taskSubmissionId) {
        TaskSubmissionDto taskSubmissionDto = taskSubmissionService.findTaskSubmission(taskSubmissionId);
        return taskSubmissionService.downloadFile(taskSubmissionDto.getFilePath());
    }
}
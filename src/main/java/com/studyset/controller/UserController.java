package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.memo.MemoDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.dto.user.UserDto;
import com.studyset.service.GroupService;
import com.studyset.service.JoinService;
import com.studyset.service.MemoService;
import com.studyset.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final GroupService groupService;
    private final JoinService joinService;
    private final MemoService memoService;
    private final TaskService taskService;

    // 유저 메인 페이지 및 그룹 조회
    @GetMapping("/users/main")
    public String userMain(HttpServletRequest request, @PageableDefault(page = 0, size = 10) Pageable pageable, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 그룹 정보 조회 및 추가
        Page<GroupDto> groups = groupService.getUserGroupList(user, pageable);
        model.addAttribute("groups", groups.getContent());
        model.addAttribute("currentPage", groups.getNumber());
        model.addAttribute("totalPages", groups.getTotalPages());
        model.addAttribute("totalItems", groups.getTotalElements());

        return "thyme/user/userMain";
    }

    // 그룹 메인 이동
    @GetMapping("/groups/{groupId}")
    public String groupMain(@SessionAttribute("user") User user, @PathVariable Long groupId, Model model, HttpSession session) {
        // 그룹 일정 가져오기

        // 현재 진행 과제 가져오기
        List<TaskDto> taskList = taskService.getCurrentTasksByGroupId(groupId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        for (TaskDto task : taskList) {
            if (task.getStartTime() != null) {
                task.setStartTimeFormatted(task.getStartTime().format(formatter));
            }
            if (task.getEndTime() != null) {
                task.setEndTimeFormatted(task.getEndTime().format(formatter));
            }
        }
        model.addAttribute("taskList", taskList);

        // 회비 내역 가져오기

        // 그룹원 최신 메모 가져오기
        List<UserDto> userList = joinService.getUserByGroupId(groupId);
        List<MemoDto> memoList = memoService.getLatestMemoByGroupId(groupId);
        model.addAttribute("mem", user);
        model.addAttribute("userList", userList);
        model.addAttribute("memoList", memoList);

        // 그룹 정보
        GroupDto group = groupService.getGroupById(groupId);
        model.addAttribute("group", group);
        session.setAttribute("group", group);

        return "/thyme/group/groupMain";
    }
}

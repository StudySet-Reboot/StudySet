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

    // 유저 메인 페이지 및 그룹 조회
    @GetMapping("/users/main")
    public String userMain(HttpServletRequest request,
                           @PageableDefault(page = 0, size = 10) Pageable pageable,
                           Model model) {
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

}

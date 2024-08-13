package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.service.GroupService;
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

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final GroupService groupService;

    // 유저 메인 페이지 및 그룹 조회
    @GetMapping("/main")
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
    @GetMapping("/group/{groupId}")
    public String groupMain(@SessionAttribute("user") User user, @PathVariable Long groupId, Model model, HttpSession session) {
        GroupDto group = groupService.getGroupById(groupId);
        model.addAttribute("group", group);
        session.setAttribute("group", group);
        return "/thyme/group/groupMain";
    }
}

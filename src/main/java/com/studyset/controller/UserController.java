package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.service.GroupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final GroupService groupService;

    // 유저 메인 페이지 및 그룹 조회
    @GetMapping("/main")
    public String userMain(HttpServletRequest request, @RequestParam(defaultValue = "0") int pageNum, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 유저 정보 추가
        model.addAttribute("user", user);

        // 그룹 정보 조회 및 추가
        PageRequest pageRequest = PageRequest.of(pageNum, 8);
        Page<GroupDto> groups = groupService.getUserGroupList(user, pageRequest);
        model.addAttribute("groups", groups.getContent());
        model.addAttribute("currentPage", groups.getNumber());
        model.addAttribute("totalPages", groups.getTotalPages());
        model.addAttribute("totalItems", groups.getTotalElements());

        return "/thyme/user/userMain";
    }

}

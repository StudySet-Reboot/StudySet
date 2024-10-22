package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.service.JoinService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final JoinService joinService;

    /**
     * 메인 페이지로 이동하여 유저의 그룹을 조회합니다.
     *
     * @param request http 요청의 세션 정보
     * @param pageable 페이징 처리
     * @param model 뷰에 전달할 데이터 객체
     * @return 반환할 뷰 경로
     */
    @GetMapping("/users/main")
    public String userMain(HttpServletRequest request,
                           @PageableDefault(page = 0, size = 10) Pageable pageable,
                           Model model) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Page<GroupDto> groups = joinService.getUserGroupList(user, pageable);
        model.addAttribute("groups", groups.getContent());
        model.addAttribute("currentPage", groups.getNumber());
        model.addAttribute("totalPages", groups.getTotalPages());
        model.addAttribute("totalItems", groups.getTotalElements());

        return "thyme/user/userMain";
    }

}

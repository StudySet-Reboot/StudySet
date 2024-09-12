package com.studyset.controller;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.user.UserDto;
import com.studyset.service.DuesService;
import com.studyset.service.GroupService;
import com.studyset.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DuesController {
    private final DuesService duesService;
    private final JoinService joinService;

    @GetMapping("/groups/{groupId}/dues")
    public String getDeusPage(@PathVariable Long groupId, Model model) {
        List<UserDto> userList = joinService.getUserByGroupId(groupId);
        model.addAttribute("userList", userList);
        return "thyme/dues/dues";
    }
}

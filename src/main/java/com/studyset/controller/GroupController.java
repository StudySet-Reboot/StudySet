package com.studyset.controller;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.exception.GroupNotExist;
import com.studyset.service.GroupService;
import com.studyset.web.form.GroupCreateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.mutation.spi.BindingGroup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller()
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupController {
    private final GroupService groupService;

    //그룹 생성
    @PostMapping("/create")
    public String createGroup(@Valid @ModelAttribute("groupCreateForm") GroupCreateForm groupCreateForm, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            return "thyme/user/userMain";
        }
        groupService.createGroup(groupCreateForm);
        return "redirect:/users/main";
    }

    @PostMapping("/join")
    public String joinGroup(@SessionAttribute("user") User user, @RequestParam String groupName, @RequestParam String code, Model model){
        try {
            groupService.joinGroup(user, groupName, code);
        }catch (GroupNotExist e){
            model.addAttribute("error", e.getMessage());
            return "thyme/user/userMain";
        }
        return "redirect:/users/main";
    }
}

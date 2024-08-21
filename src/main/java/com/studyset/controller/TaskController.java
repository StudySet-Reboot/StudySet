package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller()
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    // 과제 메인 이동
    @GetMapping("/{groupId}/task")
    public String taskMain(@SessionAttribute("user") User user, @SessionAttribute("group") GroupDto group, Model model) {
        model.addAttribute("group", group);
        return "/thyme/task/taskMain";
    }

}

package com.studyset.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ScheduleController {

    @GetMapping("/groups/{groupId}/schedule")
    public String showSchedule(@PathVariable Long groupId, Model model) {
        model.addAttribute("group.id", groupId);
        return "/thyme/group/schedule/calendar";
    }
}

package com.studyset.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.studyset.dto.group.GroupDto;
import com.studyset.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {
    private final ScheduleService scheduleService;

    //main page로
    @GetMapping("/groups/{groupId}/schedules")
    public String showSchedule(@PathVariable Long groupId, @SessionAttribute GroupDto group, Model model, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) throws JsonProcessingException {
        model.addAttribute("group", group);
        return "/thyme/group/schedule/calendar";
    }

}

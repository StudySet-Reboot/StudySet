package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {
    private final ScheduleService scheduleService;

    //스캐줄 캘린더 page
    @GetMapping("/groups/{groupId}/schedules")
    public String showSchedule(@PathVariable Long groupId, @SessionAttribute GroupDto group,
                               Model model, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month){
        model.addAttribute("group", group);
        return "/thyme/group/schedule/calendar";
    }

    //스캐줄 조정 Page
    @GetMapping("/groups/{groupId}/timetables")
    public String showAdjustPage(@PathVariable Long groupId, @SessionAttribute GroupDto group, @SessionAttribute User user, Model model) {
        model.addAttribute("group", group);
        //user의 가능 시간 찾아서
        int[][] availableTime = scheduleService.getUsersAvailableTime();
        model.addAttribute("times", availableTime);
        return "/thyme/group/schedule/timetable";
    }
}

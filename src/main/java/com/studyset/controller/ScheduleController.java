package com.studyset.controller;

import com.studyset.api.request.schedule.TimeAdjustRequest;
import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 그룹의 스캐줄을 조회합니다.
     *
     * @return 그룹 캘린더 페이지
     */
    @GetMapping("/groups/{groupId}/schedules")
    public String showSchedule(){
        return "thyme/schedule/calendar";
    }

}

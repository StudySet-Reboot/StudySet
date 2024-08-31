package com.studyset.controller;

import com.studyset.api.request.schedule.TimeAdjustRequest;
import com.studyset.domain.User;
import com.studyset.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    //스캐줄 조정 Page
    @GetMapping("/groups/{groupId}/timetables")
    public String showAdjustPage(@PathVariable Long groupId, @SessionAttribute User user, Model model) {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekOfMonth = today.get(weekFields.weekOfMonth());

        model.addAttribute("month", month);
        model.addAttribute("weekOfMonth", weekOfMonth);

        //user의 가능 시간 찾아서
        boolean[][] availableTime = timeSlotService.getUsersAvailableTime(user, groupId);
        model.addAttribute("times", availableTime);
        return "/thyme/group/schedule/timetable";
    }

    @PostMapping("/groups/{groupId}/timetables")
    public String submitTimeTable(@PathVariable Long groupId, @SessionAttribute User user, @RequestBody TimeAdjustRequest timeAdjustRequest){
        timeSlotService.addTimeSlots(user, groupId, timeAdjustRequest);
        return "redirect:/groups/"+groupId+"/timetables";
    }

}

package com.studyset.controller;

import com.studyset.api.request.schedule.TimeAdjustRequest;
import com.studyset.domain.User;
import com.studyset.dto.user.UserDto;
import com.studyset.service.JoinService;
import com.studyset.service.TimeSlotService;
import com.studyset.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TimeSlotController {

    private final TimeSlotService timeSlotService;
    private final JoinService joinService;

    /**
     * 그룹의 스캐줄 조정 페이지로 이동합니다.
     *
     * @param groupId  그룹 아이디
     * @param model   그룹의 토탈 스캐줄 조정표 정보를 담은 model
     * @return 그룹의 스캐줄 조정 페이지
     */
    @GetMapping("/groups/{groupId}/timetables/view")
    public String showAdjustPage(@PathVariable Long groupId, Model model) {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekOfMonth = today.get(weekFields.weekOfMonth());
        model.addAttribute("month", month);
        model.addAttribute("weekOfMonth", weekOfMonth);

        int[][] availableTime = timeSlotService.getGroupAvailableTime(groupId);
        model.addAttribute("times", availableTime);

        List<UserDto> userDtoList = joinService.getUserByGroupId(groupId);
        model.addAttribute("userList", userDtoList);

        return "thyme/schedule/timetable";
    }

    /**
     * 사용자가 스캐줄 조정표를 작성합니다.
     *
     * @param groupId  그룹 아이디
     * @param user   로그인한 유저
     * @param model   User의 해당 그룹 스캐줄 조정표 정보를 담은 model
     * @return 그룹의 스캐줄 조정 페이지
     */
    @GetMapping("/groups/{groupId}/schedules/adjust")
    public String getUserTable(@PathVariable Long groupId,
                               @SessionAttribute User user,
                               Model model) {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekOfMonth = today.get(weekFields.weekOfMonth());
        model.addAttribute("month", month);
        model.addAttribute("weekOfMonth", weekOfMonth);

        //user의 가능 시간 찾아서
        int[][] availableTime = timeSlotService.getAvailableTime(user.getId(), groupId);
        model.addAttribute("times", availableTime);

        return "thyme/schedule/adjustTable";
    }

}

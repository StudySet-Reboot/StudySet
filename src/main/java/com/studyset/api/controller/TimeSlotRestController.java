package com.studyset.api.controller;

import com.studyset.api.request.schedule.TimeAdjustRequest;
import com.studyset.api.response.schedule.Event;
import com.studyset.domain.User;
import com.studyset.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TimeSlotRestController {

    private final TimeSlotService timeSlotService;

    /**
     * 특정 그룹의 스캐줄 조정표를 조회합니다.
     *
     * @param groupId 그룹 ID
     * @param userId    (선택) 조정표를 조회할 user의 ID
     * @return 그룹의 스캐줄 조정표 목록을 포함한 OK 응답
     */
    @GetMapping("/groups/{groupId}/timetables")
    public ResponseEntity<int[][]> getTimeSlot(@PathVariable Long groupId,
                                               @RequestParam(required = false) Long userId) {
        int[][] availableTimes = timeSlotService.getAvailableTime(userId, groupId);
        return ResponseEntity.ok(availableTimes);
    }

    /**
     * 사용자가 스캐줄 조정표를 작성합니다.
     *
     * @param groupId  그룹 아이디
     * @param user   로그인한 유저
     * @param timeAdjustRequest   User의 시간표 조정 요청 객체
     * @return 그룹의 스캐줄 조정 페이지
     */
    @PostMapping("/groups/{groupId}/timetables")
    public ResponseEntity<Void> submitTimeTable(@PathVariable Long groupId,
                                                @SessionAttribute User user,
                                                @RequestBody TimeAdjustRequest timeAdjustRequest){
        timeSlotService.addTimeSlots(user, groupId, timeAdjustRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

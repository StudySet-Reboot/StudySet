package com.studyset.api.controller;

import com.studyset.api.response.schedule.Event;
import com.studyset.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}

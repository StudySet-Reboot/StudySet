package com.studyset.api.controller;

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

    @GetMapping("/groups/{groupId}/timetables")
    public ResponseEntity<int[][]> getTimeSlot(@PathVariable Long groupId, @RequestParam(required = false) Long userId) {
        int[][] availableTimes = timeSlotService.getAvailableTime(userId, groupId);
        return ResponseEntity.ok(availableTimes);
    }

}

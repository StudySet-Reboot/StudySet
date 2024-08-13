package com.studyset.api.controller;

import com.studyset.dto.schedule.Event;
import com.studyset.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleRestController {

    private final ScheduleService scheduleService;

    @GetMapping("/groups/{groupId}/schedules/events")
    public ResponseEntity<List<Event>> showSchedule(@PathVariable Long groupId, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        List<Event> eventList = scheduleService.getGroupSchedule(groupId, year, month);
        return ResponseEntity.ok(eventList);
    }
}

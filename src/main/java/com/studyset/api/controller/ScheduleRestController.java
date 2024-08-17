package com.studyset.api.controller;

import com.studyset.api.request.schedule.ScheduleEditRequest;
import com.studyset.api.response.schedule.Event;
import com.studyset.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScheduleRestController {

    private final ScheduleService scheduleService;

    @GetMapping("/groups/{groupId}/schedules/events")
    public ResponseEntity<List<Event>> showSchedule(@PathVariable Long groupId, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        List<Event> eventList = scheduleService.getGroupSchedule(groupId, year, month);
        return ResponseEntity.ok(eventList);
    }

    @PutMapping("/groups/{groupId}/schedules/events/{scheduleId}")
    public ResponseEntity<Event> editSchedule(@PathVariable Long groupId, @PathVariable Long scheduleId, @Valid @RequestBody ScheduleEditRequest scheduleEditRequest){
        log.info(scheduleEditRequest.getTitle());
        Event event = scheduleService.editSchedule(scheduleId, scheduleEditRequest);
        return ResponseEntity.ok(event);
    }
}

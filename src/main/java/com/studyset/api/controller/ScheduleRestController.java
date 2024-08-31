package com.studyset.api.controller;

import com.studyset.api.request.schedule.ScheduleCreateForm;
import com.studyset.api.request.schedule.ScheduleEditRequest;
import com.studyset.api.response.schedule.Event;
import com.studyset.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}")
@RequiredArgsConstructor
@Slf4j
public class ScheduleRestController {

    private final ScheduleService scheduleService;

    @GetMapping("/schedules/events")
    public ResponseEntity<List<Event>> showSchedule(@PathVariable Long groupId,
                                                    @RequestParam(required = false) Integer year,
                                                    @RequestParam(required = false) Integer month) {
        List<Event> eventList = scheduleService.getGroupSchedule(groupId, year, month);
        return ResponseEntity.ok(eventList);
    }

    @PostMapping("/schedules/events")
    public ResponseEntity<Void> createSchedule(@PathVariable Long groupId,
                                               @Valid @RequestBody ScheduleCreateForm scheduleCreateForm) {
        log.info("Creating new schedule for group: {}", groupId);
        scheduleService.addSchedule(scheduleCreateForm, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/schedules/events/{scheduleId}")
    public ResponseEntity<Event> editSchedule(@PathVariable Long groupId,
                                              @PathVariable Long scheduleId,
                                              @Valid @RequestBody ScheduleEditRequest scheduleEditRequest) {
        log.info("Editing schedule: {} for group: {}", scheduleId, groupId);
        Event event = scheduleService.editSchedule(scheduleId, scheduleEditRequest);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/schedules/events/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long groupId, @PathVariable Long scheduleId) {
        log.info("Deleting schedule: {} for group: {}", scheduleId, groupId);
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }
}


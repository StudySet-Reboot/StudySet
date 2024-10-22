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

    /**
     * 특정 그룹의 스캐줄 이벤트 목록을 조회합니다.
     *
     * @param groupId 그룹 ID
     * @param year    (선택) 조회할 연도
     * @param month   (선택) 조회할 월
     * @return 그룹의 일정 목록을 포함한 {@link Event} 객체 리스트
     */
    @GetMapping("/schedules/events")
    public ResponseEntity<List<Event>> showSchedule(@PathVariable Long groupId,
                                                    @RequestParam(required = false) Integer year,
                                                    @RequestParam(required = false) Integer month) {
        List<Event> eventList = scheduleService.getGroupSchedule(groupId, year, month);
        return ResponseEntity.ok(eventList);
    }

    /**
     * 특정 그룹에 새로운 스캐줄 이벤트를 생성합니다.
     *
     * @param groupId            그룹 ID
     * @param scheduleCreateForm 일정 생성 정보를 포함한 폼 객체
     * @return 일정 생성 후 201 (Created) 상태 응답
     */
    @PostMapping("/schedules/events")
    public ResponseEntity<Void> createSchedule(@PathVariable Long groupId,
                                               @Valid @RequestBody ScheduleCreateForm scheduleCreateForm) {
        log.info("Creating new schedule for group: {}", groupId);
        scheduleService.addSchedule(scheduleCreateForm, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 특정 그룹의 기존 일정을 수정합니다.
     *
     * @param groupId            그룹 ID
     * @param scheduleId         수정할 일정 ID
     * @param scheduleEditRequest 일정 수정 요청 정보를 포함한 객체
     * @return 수정된 {@link Event} 객체
     */
    @PutMapping("/schedules/events/{scheduleId}")
    public ResponseEntity<Event> editSchedule(@PathVariable Long groupId,
                                              @PathVariable Long scheduleId,
                                              @Valid @RequestBody ScheduleEditRequest scheduleEditRequest) {
        log.info("Editing schedule: {} for group: {}", scheduleId, groupId);
        Event event = scheduleService.editSchedule(scheduleId, scheduleEditRequest);
        return ResponseEntity.ok(event);
    }

    /**
     * 특정 그룹의 기존 일정을 삭제합니다.
     *
     * @param groupId    그룹 ID
     * @param scheduleId 삭제할 일정 ID
     * @return 일정 삭제 후 204 (No Content) 상태 응답
     */
    @DeleteMapping("/schedules/events/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long groupId, @PathVariable Long scheduleId) {
        log.info("Deleting schedule: {} for group: {}", scheduleId, groupId);
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }
}


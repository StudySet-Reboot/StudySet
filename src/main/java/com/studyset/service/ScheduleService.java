package com.studyset.service;

import com.studyset.exception.GroupNotExist;
import com.studyset.exception.InvalidEndDate;
import com.studyset.api.request.schedule.ScheduleEditRequest;
import com.studyset.domain.Group;
import com.studyset.domain.Schedule;
import com.studyset.api.response.schedule.Event;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.ScheduleRepository;
import com.studyset.api.request.schedule.ScheduleCreateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;

    /**
     * 그룹에 새로운 스케줄을 추가합니다.
     *
     * @param scheduleCreateForm 스케줄 생성 정보가 담긴 폼 객체
     * @param groupId 그룹의 ID
     * @throws InvalidEndDate 종료 날짜가 시작 날짜보다 이전일 때 발생하는 예외
     * @throws GroupNotExist 주어진 ID의 그룹이 존재하지 않을 때 발생하는 예외
     */
    @Transactional
    public void addSchedule(ScheduleCreateForm scheduleCreateForm, long groupId) {
        if (scheduleCreateForm.getEndDate() != null &&
                scheduleCreateForm.getStartDate().isAfter(scheduleCreateForm.getEndDate())) {
            throw new InvalidEndDate();
        }
        Group group = groupRepository.findGroupById(groupId)
                .orElseThrow(GroupNotExist::new);
        Schedule schedule = scheduleCreateForm.toEntity();
        schedule.addGroup(group);
        scheduleRepository.save(schedule);
    }

    /**
     * 지정된 연도와 월의 그룹 스케줄 목록을 반환합니다.
     *
     * @param groupId 스케줄을 조회할 그룹의 ID
     * @param year 조회할 연도 (null일 경우 현재 연도 사용)
     * @param month 조회할 월 (null일 경우 현재 월 사용)
     * @return 조회된 스케줄을 Event 객체로 변환한 목록
     */
    @Transactional(readOnly = true)
    public List<Event> getGroupSchedule(long groupId, Integer year, Integer month) {
        if(year == null) year = LocalDateTime.now().getYear();
        if(month == null) month = LocalDateTime.now().getMonthValue();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        List<Schedule> scheduleList = scheduleRepository.findSchedulesByGroup_IdAndStartTimeBetween(groupId, start, end);
        return scheduleList.stream().map(Schedule::toEvent)
                .toList();
    }

    /**
     * 지정된 ID의 스케줄을 수정하고, 수정된 내용을 Event 객체로 반환합니다.
     *
     * @param scheduleId 수정할 스케줄의 ID
     * @param scheduleEditRequest 스케줄 수정 요청 정보가 담긴 객체
     * @return 수정된 스케줄의 Event(Dto) 객체
     * @throws InvalidEndDate 종료 날짜가 시작 날짜보다 이전일 때 발생하는 예외
     * @throws IllegalArgumentException 주어진 ID의 스케줄이 존재하지 않을 때 발생하는 예외
     */
    @Transactional
    public Event editSchedule(Long scheduleId, ScheduleEditRequest scheduleEditRequest) {
        if (scheduleEditRequest.getEndDate() != null &&
                scheduleEditRequest.getStartDate().isAfter(scheduleEditRequest.getEndDate())) {
            throw new InvalidEndDate();
        }
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(IllegalArgumentException::new);
        schedule.edit(scheduleEditRequest);
        scheduleRepository.save(schedule);
        return schedule.toEvent();
    }

    /**
     * 지정된 ID의 스케줄을 삭제합니다.
     *
     * @param scheduleId 삭제할 스케줄의 ID
     */
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
  
}

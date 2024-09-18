package com.studyset.service;

import com.studyset.exception.InvalidEndDate;
import com.studyset.domain.Group;
import com.studyset.domain.Schedule;
import com.studyset.api.response.schedule.Event;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.ScheduleRepository;
import com.studyset.api.request.schedule.ScheduleCreateForm;
import org.aspectj.util.Reflection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    ScheduleRepository scheduleRepository;
    @Mock
    GroupRepository groupRepository;

    @InjectMocks
    ScheduleService scheduleService;

    @Test
    @DisplayName("스캐줄 추가 폼을 작성하면 폼 객체가 Entity로 변환되어 성공적으로 저장됨")
    void testAddSchedule() {
        Group group = new Group();
        ScheduleCreateForm scheduleCreateForm = new ScheduleCreateForm();
        scheduleCreateForm.setTitle("schedule Title");
        scheduleCreateForm.setDescription("schedule Description");
        scheduleCreateForm.setLocation("schedule Location");
        when(groupRepository.findGroupById(1l)).thenReturn(Optional.of(group));

        scheduleService.addSchedule(scheduleCreateForm, 1l);
        ArgumentCaptor<Schedule> scheduleArgumentCaptor = ArgumentCaptor.forClass(Schedule.class);
        verify(scheduleRepository, times(1)).save(scheduleArgumentCaptor.capture());


        Schedule schedule = scheduleArgumentCaptor.getValue();
        assertNotNull(schedule);
    }

    @Test
    @DisplayName("스캐줄 생성 시 종료시간이 시작시간보다 빠르면 에러 반환")
    void testAddSchedule_ValidDate() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1);

        ScheduleCreateForm scheduleCreateForm = new ScheduleCreateForm();
        scheduleCreateForm.setTitle("title");
        scheduleCreateForm.setDescription("schedule Description");
        scheduleCreateForm.setLocation("schedule Location");
        scheduleCreateForm.setStartDate(start);
        scheduleCreateForm.setEndDate(end);

        // when & then
        assertThrows(InvalidEndDate.class, () -> {
            scheduleService.addSchedule(scheduleCreateForm, 1L);
        });
    }

    @Test
    @DisplayName("스캐줄 표에 전달할 이벤트들은 Event List로 받음")
    void testGetScheduleList() {
        // given
        int year = 2024;
        int month = 12;
        LocalDateTime now = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = YearMonth.of(year, month).atEndOfMonth().atTime(LocalTime.MAX);

        Schedule schedule = Schedule.builder()
                .title("schedule Title")
                .description("schedule Description")
                .startTime(now)
                .isImportant(true)
                .build();
        ReflectionTestUtils.setField(schedule, "id", 1L);
        when(scheduleRepository.findSchedulesByGroup_IdAndStartTimeBetween(1l, start, end))
                .thenReturn(List.of(schedule));

        List<Event> eventList =
                scheduleService.getGroupSchedule(1L, year, month);

        assertEquals(eventList.size(), 1);
        assertEquals(eventList.get(0).getTitle(), "schedule Title");
    }

}
package com.studyset.api.controller;

import com.studyset.domain.User;
import com.studyset.dto.schedule.Event;
import com.studyset.service.ScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest
class ScheduleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @Configuration
    static class SecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorizeRequests ->
                            authorizeRequests.anyRequest().permitAll() // 모든 요청을 허용
                    ).csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }

    @Test
    @DisplayName("스케줄 리스트 호출은 JSON으로 응답함")
    void getSchedule() throws Exception {
        Long groupId = 1L;
        Integer year = 2024;
        Integer month = 8;
        User user = User.builder()
                .name("user")
                .email("test@test.com")
                .build();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        Event event = Event.builder()
                .title("Event Title")
                .start(LocalDateTime.of(year, month, 1, 0, 0))
                .build();

        when(scheduleService.getGroupSchedule(groupId, year, month))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/groups/1/schedules/events")
                        .param("year", "2024")
                        .param("month", "8")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Event Title"))
                .andDo(print()); // 디버깅에 유용하지만, 실제 배포 환경에서는 제거할 수 있음
    }
}

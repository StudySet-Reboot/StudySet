package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.exception.hanlder.RestExceptionHandler;
import com.studyset.service.JoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Mock
    private JoinService joinService;

    private MockMvc mockMvc;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L); // 필요한 사용자 정보 설정

        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(joinService))
                .setControllerAdvice(new RestExceptionHandler()) // 전역 예외 처리기 설정
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build(); // UserController와 JoinService를 설정
    }

    @Test
    @DisplayName("유저 메인 페이지 조회 성공")
    void userMain_Success() throws Exception {
        List<GroupDto> groupDtoList = new ArrayList<>();
        groupDtoList.add(new GroupDto()); // 필요한 GroupDto의 필드 설정
        Pageable pageable = PageRequest.of(0, 10);

        // Page<GroupDto> 생성
        Page<GroupDto> groups = new PageImpl<>(groupDtoList, pageable, groupDtoList.size());

        // JoinService의 getUserGroupList 메서드 Mocking
        when(joinService.getUserGroupList(user, pageable)).thenReturn(groups);

        // when & then
        mockMvc.perform(get("/users/main")
                        .sessionAttr("user", user) // 유저 세션 추가
                        .param("page", "0") // 페이지 매개변수 추가
                        .param("size", "10")) // 페이지 크기 매개변수 추가
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("groups"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalItems"))
                .andExpect(view().name("thyme/user/userMain")); // 뷰 이름 확인
    }

    @Test
    @DisplayName("유저 세션이 없으면 로그인 페이지로 리다이렉트")
    void userMain_NoUserSession_RedirectToLogin() throws Exception {

        // when & then
        mockMvc.perform(get("/users/main"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}

package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.service.JoinService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private JoinService joinService;

    private User user;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L); // 필요한 사용자 정보 설정

        request = new MockHttpServletRequest();
        request.getSession().setAttribute("user", user);
    }

    @Test
    @DisplayName("유저 메인 페이지 조회 성공")
    void userMain_Success() throws Exception {
        // given
        List<GroupDto> groupDtoList = new ArrayList<>();
        groupDtoList.add(new GroupDto());

        Page<GroupDto> groups = new PageImpl<>(groupDtoList);
        when(joinService.getUserGroupList(ArgumentMatchers.any(User.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(groups);

        // when & then
        mockMvc.perform(get("/users/main")
                        .sessionAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("groups"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalItems"));
    }

    @Test
    @DisplayName("유저 세션이 없으면 로그인 페이지로 리다이렉트")
    void userMain_NoUserSession_RedirectToLogin() throws Exception {
        // given
        request.getSession().invalidate();

        // when & then
        mockMvc.perform(get("/users/main")
                        .requestAttr("request", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }
}

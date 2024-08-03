package com.studyset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyset.api.exception.GroupNotExist;
import com.studyset.domain.User;
import com.studyset.service.GroupService;
import com.studyset.web.form.GroupCreateForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GroupService groupService;
    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new GroupController(groupService))
                .setControllerAdvice(new ExceptionController())
                .build();
    }

    @Test
    public void testCreateGroupSuccess() throws Exception {
        //given
        //when
        mockMvc.perform(post("/groups/create")
                        .param("groupName", "Test Group")
                        .param("description", "This is a test group"))

        //then
                .andExpect(status().is3xxRedirection())
                .andDo(print());
        verify(groupService, times(1)).createGroup(any(GroupCreateForm.class));
    }


    @Test
    @DisplayName("그룹에 가입에 성공하면 성공 응답이 내려옴")
    void testJoinGroupSuccess() throws Exception {
        User user = createUser();  // User 객체를 적절히 초기화하세요
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/groups/join")
                        .session(session)
                        .param("groupName", "exampleGroup")
                        .param("code", "123411")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        verify(groupService).joinGroup(user, "exampleGroup", "123411");
    }

    @Test
    @DisplayName("그룹에 가입하려면 그룹이름과 코드는 필수값")
    void testJoinGroupSuccessFail() throws Exception {
        //given
        User user = createUser();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        mockMvc.perform(post("/groups/join")
                        .session(session)
                        .param("groupName", "Test Group")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                        .andDo(print());

        verify(groupService, times(0)).joinGroup(user, "groupName", null);
    }

    @Test
    @DisplayName("그룹에 가입하려면 그룹이름과 코드에 대응되는 그룹이 있어야함")
    void testJoinNotExistGroup() throws Exception {
        //given
        User user = createUser();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);

        doThrow(new GroupNotExist())
                .when(groupService).joinGroup(user, "Test Group", "123132");

        mockMvc.perform(post("/groups/join")
                        .session(session)
                        .param("groupName", "Test Group")
                        .param("code", "123132")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    private User createUser() {
        return User.builder()
                .name("Test User")
                .email("test@test.com")
                .build();
    }
}
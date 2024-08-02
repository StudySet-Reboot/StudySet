package com.studyset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyset.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GroupService groupService;
    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(groupService)).build();
    }

    @Test
    public void test() throws Exception {
        //given
        //when
        /*
        mockMvc.perform(post("/groups/create")
                        .param("groupName", "Test Group")
                        .param("description", "This is a test group"))

                //then
                .andExpect(status().is3xxRedirection())
                .andDo(print());
        verify(groupService, times(1)).createGroup(any(GroupCreateForm.class)); */
    }
}

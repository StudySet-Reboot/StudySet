package com.studyset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.domain.enumerate.GroupCategory;
import com.studyset.dto.memo.MemoDto;
import com.studyset.exception.hanlder.RestExceptionHandler;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.UserRepository;
import com.studyset.service.GroupService;
import com.studyset.service.JoinService;
import com.studyset.service.MemoService;
import com.studyset.web.form.MemoCreateForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class MemoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemoService memoService;
    @MockBean
    private GroupService groupService;
    @MockBean
    private JoinService joinService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private GroupRepository groupRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MemoController(memoService, groupService, joinService))
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("진행상황(메모) 작성 성공")
    public void addMemo() throws Exception {
        User user = new User(9L, "John Doe", "aa@gmail.com", "google", "");
        Group group = new Group(9L, "Spring", GroupCategory.DESIGN, "aa", "123456");

        MemoCreateForm form = new MemoCreateForm();
        form.setUserId(9L);
        form.setGroupId(9L);
        form.setContent("This is a test memo.");

        given(userRepository.findById(9L)).willReturn(Optional.of(user));
        given(groupRepository.findById(9L)).willReturn(Optional.of(group));

        MemoDto mockMemoDto = MemoDto.builder()
                .userId(user.getId())
                .groupId(group.getId())
                .contents("This is a test memo.")
                .build();

        given(memoService.addMemo(form)).willReturn(mockMemoDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/groups/memo/add-memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.newMemo.contents").value("This is a test memo."));
    }
}

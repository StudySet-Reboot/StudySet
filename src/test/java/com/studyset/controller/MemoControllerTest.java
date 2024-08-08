package com.studyset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.domain.enumerate.GroupCategory;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.memo.MemoDto;
import com.studyset.dto.user.UserDto;
import com.studyset.service.GroupService;
import com.studyset.service.JoinService;
import com.studyset.service.MemoService;
import com.studyset.web.form.MemoCreateForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class MemoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemoService memoService; // 실제 서비스 빈을 주입받습니다.
    @Autowired
    private GroupService groupService;
    @Autowired
    private JoinService joinService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MemoController(memoService, groupService, joinService))
                .setControllerAdvice(new ExceptionController())
                .build();
    }

    @Test
    public void addMemo_ShouldReturnNewMemo() throws Exception {
        UserDto userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setName("John Doe");
        userDto1.setProvider("google");
        userDto1.setEmail("john.doe@example.com");

        GroupDto groupDto1 = new GroupDto();
        groupDto1.setId(1L);
        groupDto1.setGroupName("spring");
        groupDto1.setCode("123456");
        groupDto1.setCategory(GroupCategory.valueOf("DESIGN"));
        groupDto1.setDescription("aa");

        MemoDto mockMemoDto = MemoDto.builder()
                .user(userDto1.toMember())
                .group(groupDto1.toGroup())
                .contents("This is a test memo.")
                .build();

        // Mock MemoCreateForm
        MemoCreateForm form = new MemoCreateForm();
        form.setUserId(1L);
        form.setGroupId(1L);
        form.setContent("This is a test memo.");


        // Mock MemoService behavior
        given(memoService.addMemo(form)).willReturn(mockMemoDto);

        // Perform the request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/memo/addMemo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.newMemo.id").value(1))
                .andExpect(jsonPath("$.newMemo.contents").value("This is a test memo."));
    }
}

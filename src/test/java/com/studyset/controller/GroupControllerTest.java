package com.studyset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyset.api.exception.DuplicateGroup;
import com.studyset.api.exception.GroupNotExist;
import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.user.UserDto;
import com.studyset.service.GroupService;
import com.studyset.web.form.GroupCreateForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private GroupController groupController;
    @Mock
    private GroupService groupService;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new GroupController(groupService))
                .setControllerAdvice(new ExceptionController())
                .build();
    }

    @Test
    @DisplayName("그룹 생성 성공하면 생성자도 가입 처리")
    public void testCreateGroupSuccess() throws Exception {
        // Given
        User user = new User();
        GroupCreateForm groupCreateForm = new GroupCreateForm();

        // When
        groupController.createGroup(user, groupCreateForm, null, model);

        // Then
        verify(groupService, times(1)).createGroup(user, groupCreateForm);
        verifyNoInteractions(model);
    }

    @Test
    @DisplayName("이미 존재하는 그룹 생성하면 에러 처리")
    public void testCreateDuplicateGroup() throws Exception {
        //given
        GroupCreateForm groupCreateForm = new GroupCreateForm();
        User user = createUser();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        doThrow(new DuplicateGroup())
                .when(groupService).createGroup(user, groupCreateForm);

        //when
        mockMvc.perform(post("/groups/create")
                        .session(session)
                        .param("groupName", groupCreateForm.getGroupName())
                        .param("description", groupCreateForm.getDescription())
                        .param("code", groupCreateForm.getCode()))

        //then
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    @DisplayName("그룹에 가입에 성공하면 성공 응답이 내려옴")
    void testJoinGroupSuccess() throws Exception {
        User user = createUser();
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

    @Test
    @DisplayName("그룹 검색하면 검색 결과")
    void testSearchGroup() throws Exception {
        // Given
        User user = new User(); // 가정: User 객체를 생성하고 필요한 값을 설정합니다.
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 10);
        GroupDto groupDto = GroupDto.builder()
                .groupName("test group")
                .code("111111")
                .build();
        List<GroupDto> groupList = Arrays.asList(groupDto);
        Page<GroupDto> searchResults = new PageImpl<>(groupList, pageable, groupList.size());

        when(groupService.searchUserGroup(any(User.class), eq(keyword), any(Pageable.class))).thenReturn(searchResults);

        // When
        String viewName = groupController.searchList(user, keyword, pageable, model);

        // Then
        assertEquals("/thyme/user/userMain", viewName);
        verify(model, times(1)).addAttribute("groups", searchResults.getContent());
        verify(model, times(1)).addAttribute("totalPages", searchResults.getTotalPages());
        verify(model, times(1)).addAttribute("currentPage", pageable.getPageNumber());
        verify(model, times(1)).addAttribute("keyword", keyword);
    }

    private User createUser() {
        return User.builder()
                .name("Test User")
                .email("test@test.com")
                .build();
    }

    @Test
    @DisplayName("이름 검색하면 유저 검색 결과")
    void testSearchUser() throws Exception {
        // Given
        Long groupId = 1L;
        String keyword = "J";

        UserDto userDto1 = new UserDto();
        userDto1.setName("John Doe");
        userDto1.setProvider("google");
        userDto1.setEmail("john.doe@example.com");

        UserDto userDto2 = new UserDto();
        userDto2.setName("Jane Smith");
        userDto2.setProvider("facebook");
        userDto2.setEmail("jane.smith@example.com");

        List<UserDto> userDtoList = Arrays.asList(userDto1, userDto2);

        // When
        when(groupService.getUserById(groupId, keyword)).thenReturn(userDtoList);
        String viewName = groupController.searchMember(groupId, keyword, model);

        // Then
        assertEquals("thyme/fragments/userSearchResult :: userSearchResult", viewName);
        verify(model, times(1)).addAttribute("userList", userDtoList); // Check the list attribute
        verify(model, times(1)).addAttribute("keyword", keyword);

        // Verify the size of the userList
        ArgumentCaptor<List<UserDto>> userListCaptor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("userList"), userListCaptor.capture());
        List<UserDto> capturedUserList = userListCaptor.getValue();
        assertNotNull(capturedUserList, "User list should not be null");
        assertEquals(2, capturedUserList.size(), "User list size should be 2");
    }

}
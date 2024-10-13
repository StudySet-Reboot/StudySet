package com.studyset.service;

import com.studyset.exception.DuplicateGroup;
import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.domain.UserJoinGroup;
import com.studyset.domain.enumerate.GroupCategory;
import com.studyset.dto.group.GroupDto;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.UserJoinGroupRepository;
import com.studyset.web.form.GroupCreateForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserJoinGroupRepository userJoinGroupRepository;
    @InjectMocks
    private GroupService groupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUser() {
        return User.builder()
                .name("Test User")
                .email("test@test.com")
                .phone("010-1111-1111")
                .build();
    }

    private List<Group> createGroup(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> Group.builder()
                        .groupName("그룹" + i)
                        .category(GroupCategory.PROGRAMMING)
                        .description(i + "번째 그룹입니다")
                        .code(i + "2311" + i)
                        .build())
                .toList();
    }

    @Test
    @DisplayName("그룹 생성 성공")
    void testCreateGroupSuccess() {
        // Given
        GroupCreateForm form = new GroupCreateForm();
        form.setGroupName("Test Group");
        form.setCategory(GroupCategory.PROGRAMMING);
        form.setDescription("A group for science enthusiasts");
        form.setCode("111111");
        User user = createUser();

        // When
        groupService.createGroup(user, form);

        // Then
        ArgumentCaptor<Group> groupCaptor = ArgumentCaptor.forClass(Group.class);
        ArgumentCaptor<UserJoinGroup> joinCaptor = ArgumentCaptor.forClass(UserJoinGroup.class);
        verify(groupRepository, times(1)).save(groupCaptor.capture());
        verify(userJoinGroupRepository, times(1)).save(joinCaptor.capture());
        Group savedGroup = groupCaptor.getValue();
        assertEquals("Test Group", savedGroup.getGroupName());
        assertEquals(GroupCategory.PROGRAMMING, savedGroup.getCategory());
        assertEquals("A group for science enthusiasts", savedGroup.getDescription());
    }

    @Test
    @DisplayName("존재하는 그룹 생성 실패")
    void testCreateDuplicateGroup() {
        // Given
        GroupCreateForm form = new GroupCreateForm();
        form.setGroupName("Test Group");
        form.setCategory(GroupCategory.PROGRAMMING);
        form.setDescription("A group for science enthusiasts");
        form.setCode("111111");
        User user = createUser();
        when(groupRepository.findByGroupNameAndCode(form.getGroupName(), form.getCode()))
                .thenReturn(Optional.of(form.toEntity()));
        // Expected
        assertThrows(DuplicateGroup.class, () -> {
            groupService.createGroup(user, form);
        });
    }

    @Test
    @DisplayName("Group 검색 성공")
    void testSearchGroups(){
        User user = createUser();
        List<Group> group = createGroup(3);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Group> groups = new PageImpl<>(group, pageable, 3);

        when(groupRepository.findGroupsByGroupNameIsContaining("그룹", pageable)).thenReturn(groups);
        Page<GroupDto> result = groupService.searchGroup("그룹", pageable);

        assertEquals("그룹0", result.getContent().get(0).getGroupName());
        assertEquals(3, result.getTotalElements());
    }
}

package com.studyset.service;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.domain.UserJoinGroup;
import com.studyset.domain.enumerate.GroupCategory;
import com.studyset.dto.group.GroupDto;
import com.studyset.exception.GroupNotExist;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.UserJoinGroupRepository;
import com.studyset.repository.UserRepository;
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
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@Transactional
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
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
    void testCreateGroup() {
        // Given
        GroupCreateForm form = new GroupCreateForm();
        form.setGroupName("Test Group");
        form.setCategory(GroupCategory.PROGRAMMING);
        form.setDescription("A group for science enthusiasts");

        // When
        groupService.createGroup(form);

        // Then
        ArgumentCaptor<Group> groupCaptor = ArgumentCaptor.forClass(Group.class);
        verify(groupRepository, times(1)).save(groupCaptor.capture());
        Group savedGroup = groupCaptor.getValue();

        assertEquals("Test Group", savedGroup.getGroupName());
        assertEquals(GroupCategory.PROGRAMMING, savedGroup.getCategory());
        assertEquals("A group for science enthusiasts", savedGroup.getDescription());
    }

    @Test
    @DisplayName("User의 전체 그룹 가져오기")
    void testUserGroupList() {
        // Given
        List<Group> groupList = createGroup(10);

        User user = createUser();
        when(userJoinGroupRepository.findGroupsByUserId(user.getId(), PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(groupList, PageRequest.of(0, 10), groupList.size()));

        // When
        Page<GroupDto> result = groupService.getUserGroupList(user, PageRequest.of(0, 10));

        // Then
        assertEquals(10, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(groupList.size(), result.getContent().size());
        assertEquals("그룹0", result.getContent().get(0).getGroupName());
    }

    @Test
    @DisplayName("User가 가입한 그룹 내에서 검색")
    void testSearchUserGroup() {
        // Given
        List<Group> groupList = createGroup(5);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Group> groups = new PageImpl<>(groupList, pageable, groupList.size());
        User user = createUser();
        when(userJoinGroupRepository.findUserGroupBySearch(user.getId(), "그룹", pageable))
                .thenReturn(groups);

        // When
        Page<GroupDto> result = groupService.searchUserGroup(user, "그룹", pageable);

        // Then
        assertEquals(5, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals("그룹0", result.getContent().get(0).getGroupName());
    }

    @Test
    @DisplayName("그룹 가입은 그룹명과 코드가 일치하는 그룹이 있어야 함")
    void testUserJoinGroup() {
        User user = createUser();
        userRepository.save(user);
        List<Group> groupList = createGroup(5);
        when(groupRepository.findByGroupNameAndCode("그룹0", "023110"))
                .thenReturn(Optional.of(groupList.get(0)));
        ArgumentCaptor<UserJoinGroup> joinGroupArgumentCaptor = ArgumentCaptor.forClass(UserJoinGroup.class);

        //when
        groupService.joinGroup(user, "그룹0", "023110");

        //then
        verify(userJoinGroupRepository, times(1)).save(joinGroupArgumentCaptor.capture());
    }

    @Test
    @DisplayName("그룹명과 그룹 코드가 일치하는 그룹이 없으면 에러")
    void testFailUserJoinGroup() {
        // Given
        User user = createUser();
        String groupName = "testGroup";
        String code = "1234";

        // When
        when(groupRepository.findByGroupNameAndCode(groupName, code)).thenReturn(Optional.empty());

        // Then
        assertThrows(GroupNotExist.class, () -> {
            groupService.joinGroup(user, groupName, code);
        });

        verify(groupRepository, times(1)).findByGroupNameAndCode(groupName, code);
        verifyNoMoreInteractions(userJoinGroupRepository);
    }
}

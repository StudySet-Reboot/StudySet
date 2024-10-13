package com.studyset.service;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.domain.UserJoinGroup;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.user.UserDto;
import com.studyset.exception.AlreadyJoin;
import com.studyset.exception.GroupNotExist;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.UserJoinGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JoinServiceTest {

    @InjectMocks
    private JoinService joinService;

    @Mock
    private UserJoinGroupRepository joinGroupRepository;

    @Mock
    private GroupRepository groupRepository;

    private User user;
    private Group group;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .name("Test User")
                .email("test@test.com")
                .id(1L)
                .build();

        group = Group.builder()
                .groupName("Test Group")
                .id(1L)
                .code("631211")
                .build();
    }

    @Test
    @DisplayName("그룹 ID로 사용자 목록 조회")
    void getUserByGroupId_ReturnsUserDtoList() {
        when(joinGroupRepository.findUsersByGroupId(group.getId())).thenReturn(Arrays.asList(user));

        // When
        List<UserDto> result = joinService.getUserByGroupId(group.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId()); // UserDto에서 ID를 가져오는 방식에 맞춰야 함
    }

    @Test
    @DisplayName("그룹에 가입 시 사용자가 이미 가입한 경우 예외 발생")
    void joinGroup_WhenUserAlreadyJoined_ThrowsAlreadyJoinException() {
        //given
        when(groupRepository.findByGroupNameAndCode(group.getGroupName(), group.getCode())).thenReturn(Optional.of(group));
        when(joinGroupRepository.countUserJoinGroupByUserAndGroup(user, group)).thenReturn(1);

        // When & Then
        assertThrows(AlreadyJoin.class, () -> {
            joinService.joinGroup(user, group.getGroupName(), group.getCode());
        });

        verify(joinGroupRepository, never()).save(any(UserJoinGroup.class));
    }

    @Test
    @DisplayName("그룹에 가입 시 그룹이 존재하지 않으면 예외 발생")
    void joinGroup_WhenGroupNotExist_ThrowsGroupNotExistException() {
        // Given
        when(groupRepository.findByGroupNameAndCode(group.getGroupName(), group.getCode())).thenReturn(Optional.empty()); // 그룹 없음

        // When & Then
        assertThrows(GroupNotExist.class, () -> {
            joinService.joinGroup(user, group.getGroupName(), group.getCode());
        });

        verify(joinGroupRepository, never()).save(any(UserJoinGroup.class)); // 사용자가 가입되지 않아야 함
    }

    @Test
    @DisplayName("그룹에 가입 성공")
    void joinGroup_WhenValidUserAndGroup_Success() {
        // Given
        when(groupRepository.findByGroupNameAndCode(group.getGroupName(), group.getCode())).thenReturn(Optional.of(group));
        when(joinGroupRepository.countUserJoinGroupByUserAndGroup(user, group)).thenReturn(0); // 가입하지 않은 경우

        // When
        joinService.joinGroup(user, group.getGroupName(), group.getCode());

        // Then
        verify(joinGroupRepository, times(1)).save(any(UserJoinGroup.class)); // 사용자가 가입되어야 함
    }

    @Test
    @DisplayName("유저가 가입한 모든 그룹 리스트를 페이지로 반환한다.")
    void testGetUserGroupList() {
        // Given
        List<Group> groups = Arrays.asList(group);
        Page<Group> groupPage = new PageImpl<>(groups);

        when(joinGroupRepository.findGroupsByUserId(user.getId(), Pageable.unpaged())).thenReturn(groupPage);

        // When
        Page<GroupDto> result = joinService.getUserGroupList(user, Pageable.unpaged());

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(group.getGroupName(), result.getContent().get(0).getGroupName());
    }

    @Test
    @DisplayName("유저가 가입한 그룹을 검색하여 페이지로 반환한다.")
    void testSearchUserGroup() {
        // Given
        List<Group> groups = Arrays.asList(group);
        Page<Group> groupPage = new PageImpl<>(groups);

        when(joinGroupRepository.findUserGroupBySearch(user.getId(), "keyword", Pageable.unpaged()))
                .thenReturn(groupPage);

        // When
        Page<GroupDto> result = joinService.searchUserGroup(user, "keyword", Pageable.unpaged());

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }
}

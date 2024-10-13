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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserJoinGroupRepository joinGroupRepository;
    private final GroupRepository groupRepository;

    /**
     * 그룹 ID를 통해 그룹에 속한 사용자 목록을 조회합니다.
     *
     * @param groupId 그룹의 ID
     * @return List<UserDto> 그룹에 속한 사용자 정보
     */
    public List<UserDto> getUserByGroupId(Long groupId) {
        List<User> users = joinGroupRepository.findUsersByGroupId(groupId);
        return users.stream().map(UserDto::fromUser).collect(Collectors.toList());
    }

    /**
     * 유저를 그룹에 가입시킵니다.
     *
     * @param user      가입할 유저
     * @param groupName 그룹 이름
     * @param code      그룹 가입 코드
     */
    @Transactional
    public void joinGroup(User user, String groupName, String code) {
        Group group = groupRepository.findByGroupNameAndCode(groupName, code)
                .orElseThrow(GroupNotExist::new);

        if (joinGroupRepository.countUserJoinGroupByUserAndGroup(user, group) > 0) {
            throw new AlreadyJoin();
        }

        UserJoinGroup userJoinGroup = UserJoinGroup
                .builder()
                .user(user)
                .group(group)
                .build();
        joinGroupRepository.save(userJoinGroup);
    }

    /**
     * 유저가 가입한 모든 그룹 리스트를 페이지로 반환합니다.
     *
     * @param user     그룹 목록을 조회할 유저
     * @param pageable 페이징 정보
     * @return Page<GroupDto> 객체
     */
    public Page<GroupDto> getUserGroupList(User user, Pageable pageable) {
        Page<Group> groupPage = joinGroupRepository.findGroupsByUserId(user.getId(), pageable);
        List<GroupDto> dtoList = mapToDto(groupPage);
        return new PageImpl<>(dtoList, groupPage.getPageable(), groupPage.getTotalElements());
    }


    /**
     * 유저가 가입한 그룹을 검색하여 페이지로 반환합니다.
     *
     * @param user     그룹을 검색할 유저
     * @param keyword  검색 키워드
     * @param pageable 페이징 정보
     * @return Page<GroupDto> 객체
     */
    @Transactional(readOnly = true)
    public Page<GroupDto> searchUserGroup(User user, String keyword, Pageable pageable) {
        Page<Group> groupPage = joinGroupRepository.findUserGroupBySearch(user.getId(), keyword, pageable);
        List<GroupDto> dtoList = mapToDto(groupPage);
        return new PageImpl<>(dtoList, groupPage.getPageable(), groupPage.getTotalElements());
    }

    /**
     * 그룹 리스트를 Dto로 변환합니다.
     *
     * @param groupPage  그룹 Page
     * @return Page<GroupDto> 변환된 Dto Page
     */
    public List<GroupDto> mapToDto(Page<Group> groupPage) {
        List<GroupDto> dtoList = groupPage.getContent().stream()
                .map(group -> group.toDto())
                .collect(Collectors.toList());
        return dtoList;
    }
}

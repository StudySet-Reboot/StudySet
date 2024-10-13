package com.studyset.service;

import com.studyset.domain.*;
import com.studyset.dto.dues.DuesInfo;
import com.studyset.dto.group.GroupDashboard;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.memo.MemoDto;
import com.studyset.dto.task.TaskDto;
import com.studyset.dto.user.UserDto;
import com.studyset.exception.AlreadyJoin;
import com.studyset.exception.DuplicateGroup;
import com.studyset.exception.GroupCodeError;
import com.studyset.exception.GroupNotExist;
import com.studyset.repository.*;
import com.studyset.web.form.GroupCreateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserJoinGroupRepository joinGroupRepository;
    private final TaskRepository taskRepository;
    private final MemoRepository memoRepository;
    private final DuesRepository duesRepository;

    /**
     * 그룹 대시보드에서 그룹의 과제, 메모, 회비 정보를 조회합니다.
     *
     * @param groupId 그룹 ID
     * @return GroupDashboard 객체
     */
    public GroupDashboard getGroupDashboard(Long groupId) {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        // 현재 과제 목록 가져오기
        List<TaskDto> taskList = taskRepository.findCurrentTasksByGroupId(groupId, LocalDate.now())
                .stream().map(Task::toDto)
                .toList();

        // 최신 메모 목록 가져오기
        List<MemoDto> memoList = memoRepository.findLatestMemoByGroupId(groupId)
                .stream().map(Memo::toDto)
                .toList();

        // 이번 달 회비 납부 정보
        DuesInfo duesInfo = duesRepository.findDuesInfoByGroupIdAndYearAndMonth(groupId, year, month)
                .orElse(new DuesInfo(0L, 0.0d));

        return GroupDashboard.builder()
                .groupId(groupId)
                .duesInfo(duesInfo)
                .taskList(taskList)
                .memoList(memoList)
                .build();
    }


    /**
     * 그룹을 생성하고 생성된 그룹에 유저를 추가합니다.
     *
     * @param user       그룹을 생성하는 유저
     * @param createForm 그룹 생성 정보
     */
    @Transactional
    public void createGroup(User user, GroupCreateForm createForm) {
        if (checkExist(createForm.getGroupName(), createForm.getCode())) {
            throw new DuplicateGroup();
        }

        Group group = createForm.toEntity();
        groupRepository.save(group);
        UserJoinGroup userJoinGroup = UserJoinGroup.builder()
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
     * 그룹 ID를 통해 그룹을 조회하여 DTO로 반환합니다.
     *
     * @param id 그룹 ID
     * @return GroupDto 객체
     */
    public GroupDto getGroupById(Long id) {
        Optional<Group> optionalGroup = groupRepository.findGroupById(id);
        Group group = optionalGroup.orElseThrow(() -> new GroupNotExist());
        return group.toDto();
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
     * 모든 그룹을 검색하여 페이지로 반환합니다.
     *
     * @param keyword  검색 키워드
     * @param pageable 페이징 정보
     * @return Page<GroupDto> 객체
     */
    @Transactional(readOnly = true)
    public Page<GroupDto> searchGroup(String keyword, Pageable pageable) {
        Page<Group> groupPage = groupRepository.findGroupsByGroupNameIsContaining(keyword, pageable);
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

    /**
     * 그룹의 중복 여부를 확인합니다.
     *
     * @param groupName 그룹 이름
     * @param code      그룹 코드
     * @return 중복 시 true, 아니면 false
     */
    public boolean checkExist(String groupName, String code) {
        Optional<Group> group = groupRepository.findByGroupNameAndCode(groupName, code);
        return group.isPresent();
    }

    /**
     * 그룹 내에서 특정 이름을 가진 유저 목록을 검색합니다.
     *
     * @param groupId 그룹 ID
     * @param keyword 검색 키워드
     * @return List<UserDto> 검색된 유저 리스트
     */
    public List<UserDto> getUserById(Long groupId, String keyword) {
        List<User> users = joinGroupRepository.findUserByGroupIdAndUserName(groupId, keyword);
        return users.stream().map(UserDto::fromUser).collect(Collectors.toList());
    }

    /**
     * 그룹 가입을 위해 그룹 코드를 검사하여 일치 여부를 반환합니다.
     *
     * @param group 검사할 그룹
     * @param code  입력된 코드
     * @return 코드 일치 여부
     */
    public boolean checkGroupCode(GroupDto group, String code) {
        return group.getCode().equals(code);
    }

    /**
     * 그룹에서 유저를 탈퇴시킵니다.
     *
     * @param userId 유저 ID
     * @param group  그룹 DTO
     * @param code   그룹 코드
     */
    @Transactional
    public void leaveGroup(Long userId, GroupDto group, String code) {
        //그룹 코드 검사
        if (checkGroupCode(group, code)) {
            //일치하면 유저 탈퇴
            joinGroupRepository.deleteUserByGroupId(userId, group.getId());
        } else {
            throw new GroupCodeError();
        }
    }

    /**
     * 그룹 ID로 그룹을 조회합니다.
     *
     * @param id 그룹 ID
     * @return Group 객체
     */
    public Group findGroupById(Long id) {
        return groupRepository.findGroupById(id)
                .orElseThrow(() -> new GroupNotExist());
    }

    /**
     * 특정 그룹에 유저가 가입되어 있는지 확인합니다.
     *
     * @param user  유저 객체
     * @param group 그룹 객체
     * @return 가입 여부
     */
    public boolean isUserMemberOfGroup(User user, Group group) {
        return joinGroupRepository.countUserJoinGroupByUserAndGroup(user, group) > 0;
    }

}

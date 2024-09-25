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

    public GroupDashboard getGroupDashboard(Long id) {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        // 현재 과제 목록 가져오기
        List<TaskDto> taskList = taskRepository.findCurrentTasksByGroupId(id, LocalDate.now())
                .stream().map(Task::toDto)
                .toList();

        // 최신 메모 목록 가져오기
        List<MemoDto> memoList = memoRepository.findLatestMemoByGroupId(id)
                .stream().map(Memo::toDto)
                .toList();

        // 이번 달 회비 납부 정보
        DuesInfo duesInfo = duesRepository.findDuesInfoByGroupIdAndYearAndMonth(id, year, month)
                .orElse(new DuesInfo(0L, 0.0d));

        return GroupDashboard.builder()
                .groupId(id)
                .duesInfo(duesInfo)
                .taskList(taskList)
                .memoList(memoList)
                .build();
    }


    //그룹 생성
    @Transactional
    public void createGroup(User user, GroupCreateForm createForm) {
        //그룹 중복 체크
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

    //user가 가입한 그룹 전체 리스트
    public Page<GroupDto> getUserGroupList(User user, Pageable pageable) {
        Page<Group> groupPage = joinGroupRepository.findGroupsByUserId(user.getId(), pageable);
        List<GroupDto> dtoList = mapToDto(groupPage);
        return new PageImpl<>(dtoList, groupPage.getPageable(), groupPage.getTotalElements());
    }

    // 그룹ID로 그룹 조회 (DTO 반환)
    public GroupDto getGroupById(Long id) {
        Optional<Group> optionalGroup = groupRepository.findGroupById(id);
        Group group = optionalGroup.orElseThrow(() -> new GroupNotExist());
        return group.toDto();
    }

    //유저가 가입한 그룹 검색
    @Transactional(readOnly = true)
    public Page<GroupDto> searchUserGroup(User user, String keyword, Pageable pageable) {
        Page<Group> groupPage = joinGroupRepository.findUserGroupBySearch(user.getId(), keyword, pageable);
        List<GroupDto> dtoList = mapToDto(groupPage);
        return new PageImpl<>(dtoList, groupPage.getPageable(), groupPage.getTotalElements());
    }

    //그룹 가입
    @Transactional
    public void joinGroup(User user, String groupName, String code) {
        Group group = groupRepository.findByGroupNameAndCode(groupName, code)
                .orElseThrow(GroupNotExist::new);
        //이미 가입한 그룹 방지
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

    //그룹 검색
    @Transactional(readOnly = true)
    public Page<GroupDto> searchGroup(String keyword, Pageable pageable) {
        Page<Group> groupPage = groupRepository.findGroupsByGroupNameIsContaining(keyword, pageable);
        List<GroupDto> dtoList = mapToDto(groupPage);
        return new PageImpl<>(dtoList, groupPage.getPageable(), groupPage.getTotalElements());
    }

    public List<GroupDto> mapToDto(Page<Group> groupPage) {
        List<GroupDto> dtoList = groupPage.getContent().stream()
                .map(group -> group.toDto())
                .collect(Collectors.toList());
        return dtoList;
    }

    //그룹 중복 체크 함수
    public boolean checkExist(String groupName, String code) {
        Optional<Group> group = groupRepository.findByGroupNameAndCode(groupName, code);
        return group.isPresent();
    }

    //그룹원 검색
    public List<UserDto> getUserById(Long groupId, String keyword) {
        List<User> users = joinGroupRepository.findUserByGroupIdAndUserName(groupId, keyword);
        return users.stream().map(UserDto::fromUser).collect(Collectors.toList());
    }

    //그룹 코드 검사
    public boolean checkGroupCode(GroupDto group, String code) {
        return group.getCode().equals(code);
    }

    //그룹 탈퇴
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

    //그룹 아이디로 그룹 찾기
    public Group findGroupById(Long id) {
        return groupRepository.findGroupById(id)
                .orElseThrow(() -> new GroupNotExist());
    }

    //그룹 인터셉터 - 특정 그룹에 세션 유저 가입 여부 판단
    public boolean isUserMemberOfGroup(User user, Group group) {
        return joinGroupRepository.countUserJoinGroupByUserAndGroup(user, group) > 0;
    }
}

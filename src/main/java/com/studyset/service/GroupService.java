package com.studyset.service;

import com.studyset.api.exception.DuplicateGroup;
import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.domain.UserJoinGroup;
import com.studyset.dto.group.GroupDto;
import com.studyset.api.exception.AlreadyJoin;
import com.studyset.api.exception.GroupNotExist;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.UserJoinGroupRepository;
import com.studyset.web.form.GroupCreateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserJoinGroupRepository joinGroupRepository;

    //그룹 생성
    @Transactional
    public void createGroup(User user, GroupCreateForm createForm){
        //그룹 중복 체크
        if(checkExist(createForm.getGroupName(), createForm.getCode())){
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

    //user가 가입한 그룹 잔체 리스트
    public Page<GroupDto> getUserGroupList(User user, Pageable pageable){
        Page<Group> groupPage = joinGroupRepository.findGroupsByUserId(user.getId(), pageable);
        List<GroupDto> dtoList = mapToDto(groupPage);
        return new PageImpl<>(dtoList, groupPage.getPageable(), groupPage.getTotalElements());
    }

    //유저가 가입한 그룹 검색
    @Transactional(readOnly = true)
    public Page<GroupDto> searchUserGroup(User user, String keyword, Pageable pageable){
        Page<Group> groupPage = joinGroupRepository.findUserGroupBySearch(user.getId(), keyword, pageable);
        List<GroupDto> dtoList = mapToDto(groupPage);
        return new PageImpl<>(dtoList, groupPage.getPageable(), groupPage.getTotalElements());
    }

    //그룹 가입
    @Transactional
    public void joinGroup(User user, String groupName, String code){
        Group group = groupRepository.findByGroupNameAndCode(groupName, code)
                .orElseThrow(GroupNotExist::new);
        //이미 가입한 그룹 방지
        if(joinGroupRepository.countUserJoinGroupByUserAndGroup(user, group)>0){
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
    public Page<GroupDto> searchGroup(String keyword, Pageable pageable){
        Page<Group> groupPage = groupRepository.findGroupsByGroupNameIsContaining(keyword, pageable);
        List<GroupDto> dtoList = mapToDto(groupPage);
        return new PageImpl<>(dtoList, groupPage.getPageable(), groupPage.getTotalElements());
    }

    public List<GroupDto> mapToDto(Page<Group> groupPage){
        List<GroupDto> dtoList = groupPage.getContent().stream()
                .map(group -> group.toDto())
                .collect(Collectors.toList());
        return dtoList;
    }

    //그룹 중복 체크 함수
    public boolean checkExist(String groupName, String code){
        Optional<Group> group = groupRepository.findByGroupNameAndCode(groupName, code);
        return group.isPresent();
    }
}

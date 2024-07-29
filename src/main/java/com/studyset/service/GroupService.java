package com.studyset.service;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.domain.UserJoinGroup;
import com.studyset.dto.group.GroupDto;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.UserJoinGroupRepository;
import com.studyset.web.form.GroupCreateForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserJoinGroupRepository joinGroupRepository;
    //그룹 생성
    @Transactional
    public void createGroup(GroupCreateForm createForm){
        Group group = createForm.toEntity();
        groupRepository.save(group);
    }

    //user가 가입한 그룹 리스트
    public Page<GroupDto> getUserGroupList(User user, Pageable pageable){
        Page<Group> groupPage = joinGroupRepository.findGroupsByUserId(user.getId(), pageable);
        List<GroupDto> dtoList = groupPage.getContent().stream()
                .map(group -> group.toDto())
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, groupPage.getPageable(), groupPage.getTotalElements());
    }
}

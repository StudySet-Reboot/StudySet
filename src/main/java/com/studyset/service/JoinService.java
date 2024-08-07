package com.studyset.service;

import com.studyset.domain.User;
import com.studyset.dto.user.UserDto;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.UserJoinGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final GroupRepository groupRepository;
    private final UserJoinGroupRepository joinGroupRepository;

    // 그룹 내 그룹원 조회
    public List<UserDto> getUserByGroupId(Long groupId) {
        List<User> users = joinGroupRepository.findUsersByGroupId(groupId);
        return users.stream().map(UserDto::fromUser).collect(Collectors.toList());
    }

}

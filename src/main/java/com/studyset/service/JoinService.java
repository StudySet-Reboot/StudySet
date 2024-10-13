package com.studyset.service;

import com.studyset.domain.User;
import com.studyset.dto.user.UserDto;
import com.studyset.repository.UserJoinGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserJoinGroupRepository joinGroupRepository;

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

}

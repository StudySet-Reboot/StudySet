package com.studyset.service;

import com.studyset.api.exception.UserNotExist;
import com.studyset.domain.User;
import com.studyset.repository.UserJoinGroupRepository;
import com.studyset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserJoinGroupRepository joinGroupRepository;
    private final UserRepository userRepository;

    // 멤버 조회
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExist());
    }

    // 이메일로 멤버 찾기
    public Optional<User> findByEmailAndProvider(String email, String provider) {
        return userRepository.findByEmailAndProvider(email, provider);
    }

    // userId로 복수의 user 찾기
    public Map<Long, User> findUsersByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}

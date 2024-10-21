package com.studyset.service;

import com.studyset.exception.UserNotExist;
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

    /**
     * ID로 회원을 조회합니다.
     *
     * @param userId 회원 ID
     * @return User 해당 회원 객체
     * @throws UserNotExist 해당 사용자가 존재하지 않을 경우
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExist());
    }

    /**
     * 이메일로 회원을 조회합니다.
     *
     * @param email 회원의 email
     * @param provider 회원 가입 시 사용한 서비스
     * @return 해당 회원 객체
     */
    public Optional<User> findByEmailAndProvider(String email, String provider) {
        return userRepository.findByEmailAndProvider(email, provider);
    }

    /**
     * 회원 ID로 복수의 회원을 조회합니다.
     *
     * @param userIds 회원 ID 리스트
     * @return 해당 회원 리스트
     */
    public Map<Long, User> findUsersByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}

package com.studyset.repository;

import com.studyset.domain.Group;
import com.studyset.domain.UserJoinGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserJoinGroupRepository extends JpaRepository<UserJoinGroup, Long> {
    //회원의 스터디 그룹 리스트 조회
    @Query("SELECT uj.group FROM UserJoinGroup uj WHERE uj.user.id = :userId")
    Page<Group> findGroupsByUserId(Long userId, Pageable pageable);
}

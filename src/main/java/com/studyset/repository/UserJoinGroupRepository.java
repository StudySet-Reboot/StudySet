package com.studyset.repository;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.domain.UserJoinGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJoinGroupRepository extends JpaRepository<UserJoinGroup, Long> {
    //회원의 스터디 그룹 리스트 조회
    @Query("SELECT uj.group FROM UserJoinGroup uj WHERE uj.user.id = :userId")
    Page<Group> findGroupsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT uj.group FROM UserJoinGroup uj WHERE uj.user.id = :userId AND uj.group.groupName LIKE CONCAT('%', :keyword, '%')")
    Page<Group> findUserGroupBySearch(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    int countUserJoinGroupByUserAndGroup(User user, Group group);

    //그룹 내 이름으로 회원 조회
    @Query("SELECT uj.user FROM UserJoinGroup uj JOIN User u ON uj.user.id = u.id WHERE uj.group.id = :groupId AND u.name LIKE CONCAT('%', :keyword, '%')")
    List<User> findUserByGroupIdAndUserName(@Param("groupId") Long groupId, @Param("keyword") String keyword);

    //그룹 내 모든 회원 조회
    @Query("SELECT u.user from UserJoinGroup u WHERE u.group.id = :groupId")
    List findUsersByGroupId(@Param("groupId") Long groupId);

    //그룹 탈퇴
    @Modifying
    @Query("DELETE FROM UserJoinGroup WHERE user.id = :userId AND group.id = :groupId")
    int deleteUserByGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
}

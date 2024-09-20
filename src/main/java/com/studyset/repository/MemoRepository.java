package com.studyset.repository;

import com.studyset.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    // 그룹 내 모든 메모 조회
    List<Memo> findByGroupId(@Param("groupId") Long groupId);

    // 그룹 내 최신 메모 조회
    @Query("SELECT m FROM Memo m WHERE m.group.id = :groupId AND m.createdDate = (SELECT MAX(subm.createdDate) FROM Memo subm WHERE subm.user.id = m.user.id AND subm.group.id = :groupId)")
    List<Memo> findLatestMemoByGroupId(@Param("groupId") Long groupId);
}

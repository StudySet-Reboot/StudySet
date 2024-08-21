package com.studyset.repository;

import com.studyset.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    // 그룹 내 모든 메모 조회
    List<Memo> findByGroupId(@Param("groupId") Long groupId);
}

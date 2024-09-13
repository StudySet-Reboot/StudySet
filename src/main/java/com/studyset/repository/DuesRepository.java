package com.studyset.repository;

import com.studyset.domain.Dues;
import com.studyset.dto.dues.DuesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface DuesRepository extends JpaRepository<Dues, Long> {
    @Query("SELECT new com.studyset.dto.dues.DuesDto(d.user.name, d.price, d.duesDate) FROM Dues d WHERE d.group.id = :groupId")
    Page<DuesDto> findAllByGroupId(@Param("groupId") Long groupId, Pageable pageable);
}

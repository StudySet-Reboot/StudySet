package com.studyset.repository;

import com.studyset.domain.Dues;
import com.studyset.dto.dues.DuesDto;
import com.studyset.dto.dues.DuesInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;


public interface DuesRepository extends JpaRepository<Dues, Long> {
    @Query("SELECT new com.studyset.dto.dues.DuesDto(d.user.name, d.price, d.duesDate) FROM Dues d WHERE d.group.id = :groupId")
    Page<DuesDto> findAllByGroupId(@Param("groupId") Long groupId, Pageable pageable);

    @Query("SELECT new com.studyset.dto.dues.DuesInfo(count(d.user), sum(d.price)) " +
            "FROM Dues d WHERE d.group.id = :groupId " +
            "AND FUNCTION('YEAR', d.duesDate) = :year " +
            "AND FUNCTION('MONTH', d.duesDate) = :month")
    Optional<DuesInfo> findDuesInfoByGroupIdAndYearAndMonth(@Param("groupId") Long groupId,
                                                            @Param("year") int year,
                                                            @Param("month") int month);


}

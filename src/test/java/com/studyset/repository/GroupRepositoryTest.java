package com.studyset.repository;

import com.studyset.domain.Group;
import com.studyset.domain.enumerate.GroupCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
class GroupRepositoryTest {
    @Autowired
    GroupRepository groupRepository;
    @Test
    @DisplayName("그룹 이름으로 검색")
    void findGroupsByGroupNameIsContaining() {
        //given
        Group group1 = Group.builder()
                .groupName("Java Study !!")
                .category(GroupCategory.PROGRAMMING)
                .description("검색 테스트")
                .code("111111")
                .build();
        Group group2 = Group.builder()
                .groupName("OPIC 스터디")
                .category(GroupCategory.LANGUAGE)
                .description("검색 테스트")
                .code("010101")
                .build();
        groupRepository.save(group1);
        groupRepository.save(group2);
        Pageable pageable = PageRequest.of(0, 10);
        //when
        Page<Group> result = groupRepository.findGroupsByGroupNameIsContaining("Java", pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals(GroupCategory.PROGRAMMING, result.getContent().get(0).getCategory());
    }
}
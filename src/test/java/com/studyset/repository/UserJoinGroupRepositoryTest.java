package com.studyset.repository;

import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.domain.UserJoinGroup;
import com.studyset.domain.enumerate.GroupCategory;
import com.studyset.dto.group.GroupDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class UserJoinGroupRepositoryTest {

    @Autowired
    private UserJoinGroupRepository userJoinGroupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    List<Group> createGroupList(int size){
        return IntStream.range(0, size)
                .mapToObj(i -> Group.builder()
                        .groupName("study" + i +" group")
                        .category(GroupCategory.PROGRAMMING)
                        .description(i + "번째 그룹입니다")
                        .build())
                .collect(Collectors.toList());
    }
    User createUser(){
        return User.builder()
                .name("test user")
                .email("test@test.com")
                .build();
    }
    @Test
    @DisplayName("유저가 가입한 그룹 이름으로 검색")
    void findUserGroupBySearch() {
        //given
        List<Group> groupList = createGroupList(10);
        groupRepository.saveAll(groupList);
        User user = createUser();
        userRepository.save(user);
        List<UserJoinGroup> joinGroupList = groupList.stream()
                .map(group -> {
                    return new UserJoinGroup(user, group);
                })
                .collect(Collectors.toList());
        userJoinGroupRepository.saveAll(joinGroupList);
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<Group> result = userJoinGroupRepository.findUserGroupBySearch(user.getId(), "study1", pageable);

        //then
        assertEquals(1, result.getTotalElements());
        assertEquals(GroupCategory.PROGRAMMING, result.getContent().get(0).getCategory());
    }
}
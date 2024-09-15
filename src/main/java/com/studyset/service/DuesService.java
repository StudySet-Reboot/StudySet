package com.studyset.service;

import com.studyset.api.exception.GroupNotExist;
import com.studyset.api.exception.UserNotExist;
import com.studyset.domain.Dues;
import com.studyset.domain.Group;
import com.studyset.domain.User;
import com.studyset.dto.dues.DuesDto;
import com.studyset.repository.DuesRepository;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.UserRepository;
import com.studyset.web.form.DuesForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DuesService {
    private final DuesRepository duesRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public Page<DuesDto> getGroupDuesList(Long groupId, Pageable pageable) {
        return duesRepository.findAllByGroupId(groupId, pageable);
    }

    public void addDues(Long groupId, DuesForm duesForm) {
        Group group = groupRepository.findGroupById(groupId)
                .orElseThrow(GroupNotExist::new);
        User user = userRepository.findById(duesForm.getUserId())
                .orElseThrow(UserNotExist::new);
        Dues dues = Dues.builder()
                .duesDate(duesForm.getDuesDate())
                .group(group)
                .user(user)
                .price(duesForm.getPrice())
                .build();
        duesRepository.save(dues);
    }
}

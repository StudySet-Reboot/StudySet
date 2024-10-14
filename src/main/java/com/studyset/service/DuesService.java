package com.studyset.service;

import com.studyset.exception.GroupNotExist;
import com.studyset.exception.UserNotExist;
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

    /**
     * 그룹에 대한 모든 회비 납부 내역을 페이지로 반환합니다.
     *
     * @param groupId 그룹의 ID
     * @param pageable 페이징 정보
     * @return Page<DuesDto> 회비 납부 내역 목록 DTO
     */
    public Page<DuesDto> getGroupDuesList(Long groupId, Pageable pageable) {
        return duesRepository.findAllByGroupId(groupId, pageable);
    }

    /**
     * 회비 납부 내역을 추가합니다.
     *
     * @param groupId  회비 납부 내역이 추가될 그룹의 ID
     * @param duesForm 회비 납부 내역 정보를 담고 있는 form 객체
     */
    //TODO: validation 처리 추가 필요
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

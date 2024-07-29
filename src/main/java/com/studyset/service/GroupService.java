package com.studyset.service;

import com.studyset.domain.Group;
import com.studyset.repository.GroupRepository;
import com.studyset.web.form.GroupCreateForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository repository;

    //그룹 생성
    @Transactional
    public void createGroup(GroupCreateForm createForm){
        Group group = createForm.toEntity();
        repository.save(group);
    }

}

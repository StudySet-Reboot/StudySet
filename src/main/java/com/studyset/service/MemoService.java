package com.studyset.service;

import com.studyset.api.exception.GroupNotExist;
import com.studyset.api.exception.UserNotExist;
import com.studyset.domain.Group;
import com.studyset.domain.Memo;
import com.studyset.domain.User;
import com.studyset.dto.memo.MemoDto;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.MemoRepository;
import com.studyset.repository.UserRepository;
import com.studyset.web.form.MemoCreateForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    // 메모 조회
    public List<MemoDto> getMemoByGroupId(Long groupId) {
        List<Memo> memoList = memoRepository.findByGroupId(groupId);
        return memoList.stream().map(Memo::toDto).collect(Collectors.toList());
    }

    // 메모 작성
    @Transactional
    public MemoDto addMemo(MemoCreateForm memoForm) {
        User user = userRepository.findById(memoForm.getUserId())
                .orElseThrow(() -> new UserNotExist());

        Group group = groupRepository.findById(memoForm.getGroupId())
                .orElseThrow(() -> new GroupNotExist());

        Memo memo = new Memo();
        memo.setUser(user);
        memo.setGroup(group);
        memo.setContents(memoForm.getContent());

        memoRepository.save(memo);

        return memo.toDto();
    }

}

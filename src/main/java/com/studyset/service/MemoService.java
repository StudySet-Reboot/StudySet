package com.studyset.service;

import com.studyset.exception.GroupNotExist;
import com.studyset.exception.UserNotExist;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    /**
     * 그룹의 메모 목록을 조회합니다.
     *
     * @param groupId 그룹 ID
     * @return List<MemoDto> 해당 그룹에 속한 메모 목록
     */
    public List<MemoDto> getMemoByGroupId(Long groupId) {
        List<Memo> memoList = memoRepository.findByGroupId(groupId);
        return memoList.stream().map(Memo::toDto).collect(Collectors.toList());
    }

    /**
     * 그룹의 최신 메모를 조회합니다.
     *
     * @param groupId 그룹 ID
     * @return List<MemoDto> 해당 그룹원의 최신 메모 목록
     */
    public List<MemoDto> getLatestMemoByGroupId(Long groupId) {
        List<Memo> memoList = memoRepository.findLatestMemoByGroupId(groupId);
        return memoList.stream().map(Memo::toDto).collect(Collectors.toList());
    }

    /**
     * 새로운 메모를 작성합니다.
     *
     * @param memoForm 메모 작성 폼
     * @return MemoDto 작된 메모 정보성
     * @throws UserNotExist 해당 사용자가 존재하지 않을 경우
     * @throws GroupNotExist 해당 그룹이 존재하지 않을 경우
     */
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

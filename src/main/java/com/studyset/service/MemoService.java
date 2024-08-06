package com.studyset.service;

import com.studyset.domain.Memo;
import com.studyset.dto.memo.MemoDto;
import com.studyset.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;

    // 메모 조회
    public List<MemoDto> getMemoByGroupId(Long groupId) {
        List<Memo> memoList = memoRepository.findByGroupId(groupId);
        return memoList.stream().map(Memo::toDto).collect(Collectors.toList());
    }

    // 메모 작성


}

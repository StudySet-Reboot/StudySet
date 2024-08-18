package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.memo.MemoDto;
import com.studyset.dto.user.UserDto;
import com.studyset.service.GroupService;
import com.studyset.service.JoinService;
import com.studyset.service.MemoService;
import com.studyset.web.form.MemoCreateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller()
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class MemoController {
    private final MemoService memoService;
    private final GroupService groupService;
    private final JoinService joinService;

    // 진행상황 메인 이동
    @GetMapping("/{groupId}/memo")
    public String memoMain(@SessionAttribute("user") User user, @PathVariable Long groupId, Model model) {
        List<UserDto> userList = joinService.getUserByGroupId(groupId);
        GroupDto groupDto = groupService.getGroupById(groupId);
        List<MemoDto> memoList = memoService.getMemoByGroupId(groupId);
        model.addAttribute("mem", user);
        model.addAttribute("userList", userList);
        model.addAttribute("memoList", memoList);
        model.addAttribute("group", groupDto);
        return "thyme/memo/memoMain";
    }

    // 메모 작성
    @PostMapping("/memo/addMemo")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addMemo(@RequestBody MemoCreateForm memo) {
        MemoDto newMemo = memoService.addMemo(memo);

        Map<String, Object> response = new HashMap<>();
        response.put("newMemo", newMemo);
        return ResponseEntity.ok(response);
    }
}

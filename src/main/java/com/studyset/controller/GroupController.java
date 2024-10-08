package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDashboard;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.user.UserDto;
import com.studyset.service.GroupService;
import com.studyset.web.form.GroupCreateForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller()
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupController {

    private final GroupService groupService;

    // 그룹 메인 이동
    @GetMapping("/{groupId}")
    public String groupMain(@PathVariable Long groupId,
                            Model model,
                            HttpSession session) {
        GroupDto groupDto = groupService.getGroupById(groupId);
        session.setAttribute("group", groupDto);
        model.addAttribute("group", groupDto);
        GroupDashboard groupDashboard = groupService.getGroupDashboard(groupId);
        model.addAttribute("groupDashboard", groupDashboard);
        return "/thyme/group/groupMain";
    }

    //그룹 생성
    @PostMapping("/create")
    @ResponseBody
    public void createGroup(@SessionAttribute("user") User user,
                            @Valid @ModelAttribute("groupCreateForm") GroupCreateForm groupCreateForm,
                            BindingResult bindingResult){
        groupService.createGroup(user, groupCreateForm);
    }

    //그룹 가입
    @PostMapping("/join")
    @ResponseBody
    public void joinGroup(@SessionAttribute("user") User user, @RequestParam String groupName, @RequestParam String code){
        groupService.joinGroup(user, groupName, code);
    }

    //그룹 검색
    @GetMapping("/search")
    public String searchList(@SessionAttribute("user") User user, @RequestParam String keyword, @PageableDefault(size = 10, page = 0) Pageable pageable, Model model) {
        Page<GroupDto> searchResults = groupService.searchUserGroup(user, keyword, pageable);
        model.addAttribute("groups", searchResults);
        model.addAttribute("groups", searchResults.getContent());
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("keyword", keyword);

        return "/thyme/user/userMain";
    }

    //그룹원 검색
    @GetMapping("/userSearch")
    public String searchMember(@RequestParam Long groupId, @RequestParam String keyword, Model model) {
        List<UserDto> userList = groupService.getUserById(groupId, keyword);
        GroupDto groupDto = groupService.getGroupById(groupId);
        model.addAttribute("userList", userList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("group", groupDto);
        return "thyme/fragments/userSearchResult :: userSearchResult";
    }

    //그룹 탈퇴
    @PostMapping("/leave")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> leaveGroup(@SessionAttribute("user") User user, @SessionAttribute("group") GroupDto group, @RequestParam String code) {
        groupService.leaveGroup(user.getId(), group, code);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

}

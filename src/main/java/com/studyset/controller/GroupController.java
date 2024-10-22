package com.studyset.controller;

import com.studyset.domain.User;
import com.studyset.dto.group.GroupDashboard;
import com.studyset.dto.group.GroupDto;
import com.studyset.dto.user.UserDto;
import com.studyset.service.GroupService;
import com.studyset.service.JoinService;
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

@Controller
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupController {

    private final GroupService groupService;
    private final JoinService joinService;

    /**
     * 유저의 그룹 메인 페이지로 이동합니다.
     *
     * @param groupId 해당 그룹 ID
     * @param model 뷰에 전달할 데이터 객체
     * @param session http 세션 객체
     * @return 반환할 뷰 경로
     */
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

    /**
     * 스터디 그룹을 생성합니다.
     *
     * @param user 현재 로그인한 유저
     * @param groupCreateForm 그룹 생성 정보가 담긴 폼
     */
    @PostMapping("/create")
    @ResponseBody
    public void createGroup(@SessionAttribute("user") User user,
                            @Valid @ModelAttribute("groupCreateForm") GroupCreateForm groupCreateForm){
        groupService.createGroup(user, groupCreateForm);
    }

    /**
     * 스터디 그룹에 가입합니다.
     *
     * @param user 현재 로그인한 유저
     * @param groupName 가입할 그룹명
     * @param code 가입할 그룹 코드
     */
    @PostMapping("/join")
    @ResponseBody
    public void joinGroup(@SessionAttribute("user") User user,
                          @RequestParam String groupName,
                          @RequestParam String code){
        joinService.joinGroup(user, groupName, code);
    }

    /**
     * 스터디 그룹을 검색합니다.
     *
     * @param user 현재 로그인한 유저
     * @param keyword 검색 키워드
     * @param pageable 페이징 처리
     * @param model 뷰에 전달할 데이터 객체
     * @return 반환할 뷰 경로
     */
    @GetMapping("/search")
    public String searchList(@SessionAttribute("user") User user,
                             @RequestParam String keyword,
                             @PageableDefault(size = 10, page = 0) Pageable pageable,
                             Model model) {
        Page<GroupDto> searchResults = joinService.searchUserGroup(user, keyword, pageable);
        model.addAttribute("groups", searchResults);
        model.addAttribute("groups", searchResults.getContent());
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("keyword", keyword);

        return "/thyme/user/userMain";
    }

    /**
     * 특정 그룹 내 그룹원을 검색합니다.
     *
     * @param groupId 특정 그룹 ID
     * @param keyword 그룹원 검색 키워드
     * @param model 뷰에 전달할 데이터 객체
     * @return 반환할 뷰 경로
     */
    @GetMapping("/userSearch")
    public String searchMember(@RequestParam Long groupId,
                               @RequestParam String keyword,
                               Model model) {
        List<UserDto> userList = groupService.getUserById(groupId, keyword);
        GroupDto groupDto = groupService.getGroupById(groupId);
        model.addAttribute("userList", userList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("group", groupDto);
        return "thyme/fragments/userSearchResult :: userSearchResult";
    }

    /**
     * 스터디 그룹을 탈퇴합니다.
     *
     * @param user 현재 로그인한 유저
     * @param group 탈퇴할 스터디 그룹
     * @param code 탈퇴 시 입력한 코드
     * @return 응답 상태와 데이터를 포함한 Map 객체
     */
    @PostMapping("/leave")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> leaveGroup(@SessionAttribute("user") User user,
                                                          @SessionAttribute("group") GroupDto group,
                                                          @RequestParam String code) {
        groupService.leaveGroup(user.getId(), group, code);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

}

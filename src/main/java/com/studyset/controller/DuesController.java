package com.studyset.controller;

import com.studyset.dto.dues.DuesDto;
import com.studyset.dto.dues.PaymentDto;
import com.studyset.dto.user.UserDto;
import com.studyset.service.DuesService;
import com.studyset.service.JoinService;
import com.studyset.service.PaymentService;
import com.studyset.web.form.DuesForm;
import com.studyset.web.form.PaymentForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DuesController {

    private final DuesService duesService;
    private final JoinService joinService;

    /**
     * 그룹의 회비 납부 내역 페이지로 이동합니다.
     *
     * @param groupId  그룹 아이디
     * @param model    회비 납부 내역을 담은 model
     * @param pageable pagination information
     * @return 그룹 회비 납부 페이지
     */
    @GetMapping("/groups/{groupId}/dues")
    public String getDeusPage(@PathVariable Long groupId, Model model,
                              @PageableDefault(size = 20) Pageable pageable) {
        List<UserDto> userList = joinService.getUserByGroupId(groupId);
        model.addAttribute("userList", userList);
        Page<DuesDto> duesList = duesService.getGroupDuesList(groupId, pageable);
        model.addAttribute("duesList", duesList);
        model.addAttribute("duesForm", new DuesForm());
        return "thyme/dues/dues";
    }

    /**
     * 회비 납부 내역 생성 후 회비 납부 페이지로 이동합니다.
     *
     * @param groupId  그룹 아이디
     * @param duesForm 회비 납부 내역 정보를 담은 Form 객체
     * @return 그룹 회비 납부 페이지
     */
    @PostMapping("/groups/{groupId}/dues")
    public String addDues(@PathVariable Long groupId,
                          @ModelAttribute DuesForm duesForm){
        duesService.addDues(groupId, duesForm);
        return "redirect:/groups/"+groupId+"/dues";
    }

}

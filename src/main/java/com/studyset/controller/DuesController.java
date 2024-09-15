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
    private final PaymentService paymentService;

    @GetMapping("/groups/{groupId}/dues")
    public String getDeusPage(@PathVariable Long groupId, Model model, @PageableDefault(size = 20) Pageable pageable) {
        List<UserDto> userList = joinService.getUserByGroupId(groupId);
        model.addAttribute("userList", userList);
        Page<DuesDto> duesList = duesService.getGroupDuesList(groupId, pageable);
        model.addAttribute("duesList", duesList);
        model.addAttribute("duesForm", new DuesForm());
        return "thyme/dues/dues";
    }

    @PostMapping("/groups/{groupId}/dues")
    public String addDues(@PathVariable Long groupId, @ModelAttribute DuesForm duesForm, Model model){
        duesService.addDues(groupId, duesForm);
        return "redirect:/groups/"+groupId+"/dues";
    }

}

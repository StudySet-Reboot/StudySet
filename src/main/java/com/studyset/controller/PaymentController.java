package com.studyset.controller;

import com.studyset.dto.dues.PaymentDto;
import com.studyset.service.PaymentService;
import com.studyset.web.form.PaymentForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/groups/{groupId}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 그룹의 회비 사용 내역 페이지로 이동합니다.
     *
     * @param groupId  그룹 아이디
     * @param model    회비 사용 내역을 담은 model
     * @param pageable pagination information
     * @return 그룹 회비 사용 내역 페이지
     */
    @GetMapping
    public String getPaymentPage(@PathVariable Long groupId,
                                 Model model,
                                 @PageableDefault(size = 20) Pageable pageable){
        Page<PaymentDto> paymentList = paymentService.getPayments(groupId, pageable);
        model.addAttribute("paymentList", paymentList);
        model.addAttribute("paymentForm", new PaymentForm());
        return "thyme/dues/payment";
    }

    /**
     * 그룹의 회비 사용 내역을 추가하고 회비 사용 납부 페이지로 이동합니다.
     *
     * @param groupId  그룹 아이디
     * @return 그룹 회비 납부 페이지
     */
    @PostMapping
    public String addPayment(@PathVariable Long groupId,
                             @ModelAttribute PaymentForm paymentForm){
        paymentService.addPayment(groupId, paymentForm);
        return "redirect:/groups/"+groupId+"/payments";
    }

}

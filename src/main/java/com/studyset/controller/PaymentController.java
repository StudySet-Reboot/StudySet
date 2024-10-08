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

    @GetMapping
    public String getPaymentPage(@PathVariable Long groupId, Model model, @PageableDefault(size = 20) Pageable pageable){
        Page<PaymentDto> paymentList = paymentService.getPayments(groupId, pageable);
        model.addAttribute("paymentList", paymentList);
        model.addAttribute("paymentForm", new PaymentForm());
        return "thyme/dues/payment";
    }

    @PostMapping
    public String addPayment(@PathVariable Long groupId, @ModelAttribute PaymentForm paymentForm){
        paymentService.addPayment(groupId, paymentForm);
        return "redirect:/groups/"+groupId+"/payments";
    }

}

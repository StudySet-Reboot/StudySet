package com.studyset.controller;

import com.studyset.web.form.GroupCreateForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    @GetMapping("/main")
    public String main(Model model){
        model.addAttribute("groupCreateForm", new GroupCreateForm());
        return "thyme/user/userMain";
    }

}

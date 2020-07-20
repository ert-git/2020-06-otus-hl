package ru.otus.hl.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ru.otus.hl.model.User;
import ru.otus.hl.service.UserService;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration.html")
    public String registration(@ModelAttribute User user) {
        userService.create(user);
        return "redirect:/login.html";
    }

    @GetMapping("/registration.html")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

}

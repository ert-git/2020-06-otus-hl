package ru.otus.hl.web.controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @RequestMapping("/")
    public String root(Locale locale) {
        return "redirect:/login.html";
    }

    @RequestMapping("/login.html")
    public String login() {
        return "login";
    }
 
    @RequestMapping("/login-error.html")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @RequestMapping("/403.html")
    public String forbidden() {
        return "403";
    }

}

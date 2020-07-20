package ru.otus.hl.web.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.otus.hl.model.User;
import ru.otus.hl.service.UserService;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user.html")
    public String userIndex(Model model, Principal principal) {
        Optional<User> user = userService.findByLogin(principal.getName());
        if (user.isPresent()) {
            userService.setInterests(user.get());
            model.addAttribute("user", user.get());
            model.addAttribute("users", userService.getAll());
            model.addAttribute("friends", userService.getFriends(user.get()));
            return "user";
        } else {
            return "redirect:/404.html";
        }
    }
    
    @GetMapping("/view-user-info.html")
    public String userInfo(@RequestParam long id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            userService.setInterests(user.get());
            model.addAttribute("user", user.get());
            model.addAttribute("friends", userService.getFriends(user.get()));
            return "user-info";
        } else {
            return "redirect:/404.html";
        }
    }
    
    
    @GetMapping("/add-friend.html")
    public String addFriend(@RequestParam long friendId, Principal principal) {
        Optional<User> user = userService.findByLogin(principal.getName());
        if (user.isPresent()) {
            userService.addFriend(user.get(), friendId);
            return "redirect:/user.html";
        } else {
            return "redirect:/404.html";
        }
    }
    
    @GetMapping("/remove-friend.html")
    public String removeFriend(@RequestParam long friendId, Principal principal) {
        Optional<User> user = userService.findByLogin(principal.getName());
        if (user.isPresent()) {
            userService.removeFriend(user.get(), friendId);
            return "redirect:/user.html";
        } else {
            return "redirect:/404.html";
        }
    }
    
    @PostMapping("/user.html")
    public String userSave(@ModelAttribute User data, Principal principal) {
        Optional<User> user = userService.findByLogin(principal.getName());
        if (user.isPresent()) {
            data.setId(user.get().getId());
            userService.update(data);
            return "redirect:/user.html";
        } else {
            return "redirect:/404.html";
        }
    }

}

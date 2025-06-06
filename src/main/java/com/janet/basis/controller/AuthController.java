package com.janet.basis.controller;

import com.janet.basis.entity.User;
import com.janet.basis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AuthController {

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/login")
  public String showLoginForm() {
    return "login";
  }

  @PostMapping("/login")
  public String login(@RequestParam String Username,
      @RequestParam String Password,
      Model model) {

    User users = userRepository.findByUsernameAndPassword(Username, Password);

    if (users != null) {
      model.addAttribute("Username", users.getUsername());
      return "home"; // Gửi tới home.html
    } else {
      model.addAttribute("error", "Invalid credentials!");
      return "login"; // Quay lại login.html
    }
  }

  @GetMapping("/home")
  public String showHome(Model model) {
    model.addAttribute("Username", "Guest");
    return "home";
  }

}

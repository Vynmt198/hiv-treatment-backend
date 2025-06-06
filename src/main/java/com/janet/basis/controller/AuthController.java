package com.janet.basis.controller;

import com.janet.basis.dto.UserLoginRequest;
import com.janet.basis.entity.Account;
import com.janet.basis.repository.UserRepository;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AuthController {

  //
//  @Autowired
//  private UserRepository userRepository;
//
//  @GetMapping("/login")
//  public String showLoginForm() {
//    return "login";
//  }
//
//  @PostMapping("/login")
//  public String login(@RequestBody UserLoginRequest userLoginRequest, Model model) {
//    String username = userLoginRequest.getUsername();
//    String password = userLoginRequest.getPassword();
//
//    Account users = userRepository.findByUsernameAndPassword(username, password);
//
//    if (users != null) {
//      model.addAttribute("Username", users.getUsername());
//      return "home"; // Gửi tới home.html
//    } else {
//      model.addAttribute("error", "Invalid credentials!");
//      return "login"; // Quay lại login.html
//    }
//  }
//
//
//  @GetMapping("/home")
//  public String showHome(Model model) {
//    model.addAttribute("Username", "Guest");
//    return "home";
//  }
  @GetMapping({"/", "/home"}) // Map cả root và /home
  public String homePage(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserName = "Guest";
    if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(
        authentication.getName())) {
      currentUserName = authentication.getName();
    }
    model.addAttribute("userName", currentUserName);
    return "home"; // Trả về view home.html
  }

}


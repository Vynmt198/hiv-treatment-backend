//package com.janet.basis.service;
//
//import com.janet.basis.dto.UserLoginRequest;
//import com.janet.basis.dto.UserLoginResponse;
//import com.janet.basis.entity.User;
//import com.janet.basis.repository.UserRepository;
//
//public class AuthService {
//
//    private UserRepository userRepository;
//
//
//    public AuthService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public UserLoginResponse login(UserLoginRequest request) {
//        User user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        if (!user.getPassword().equals(request.getPassword())) {
//            throw new BadCredentialsException("Incorrect password");
//        }
//
//        UserLoginResponse response = new UserLoginResponse();
//        response.setFullName(user.getFullName());
//        response.setRole(user.getRole().getRoleName());
//        response.setMessage("Login successful");
//
//        return response;
//    }
//}

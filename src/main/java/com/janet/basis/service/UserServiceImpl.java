package com.janet.basis.service;

import com.janet.basis.dto.UserRegistrationDto;
import com.janet.basis.entity.Account;
import com.janet.basis.entity.Role;
import com.janet.basis.repository.RoleRepository;
import com.janet.basis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository; // Repository cho Account/User

  @Autowired
  private PasswordEncoder passwordEncoder; // Bean từ SecurityConfig

  @Autowired
  private RoleRepository roleRepository; // Repository cho Role

  @Override
  public Account registerNewUser(UserRegistrationDto registrationDto) throws Exception {
    // 1. Kiểm tra xác nhận mật khẩu
    if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
      throw new Exception("Passwords do not match");
    }
    // 2. Kiểm tra username đã tồn tại chưa
    if (userRepository.existsByUsername(registrationDto.getUsername())) {
      throw new Exception("Username already exists: " + registrationDto.getUsername());
    }
    // 3. Kiểm tra email đã tồn tại chưa
    if (userRepository.existsByEmail(registrationDto.getEmail())) {
      throw new Exception("Email already exists: " + registrationDto.getEmail());
    }

    // 4. Tạo đối tượng Account mới
    Account user = new Account(); // Sử dụng tên biến 'user' một cách nhất quán
    user.setUsername(registrationDto.getUsername());
    user.setPassword(passwordEncoder.encode(registrationDto.getPassword())); // Mã hóa mật khẩu
    user.setEmail(registrationDto.getEmail());
    // Bạn có thể set các trường khác của Account ở đây nếu có, ví dụ: fullName, phone từ DTO
    // user.setFullName(registrationDto.getFullName()); // Ví dụ

    // 5. Xử lý Role
    String defaultRoleName = "ROLE_USER"; // Vai trò mặc định khi đăng ký
    Role userRole = roleRepository.findByName(defaultRoleName)
        .orElseGet(() -> {
          // Nếu Role "ROLE_USER" chưa có trong CSDL, tạo mới và lưu lại
          Role newRole = new Role();
          newRole.setRoleName(defaultRoleName); // Giả định Role entity có setName()
          return roleRepository.save(newRole);
        });

    // 6. Gán Role cho user
    user.setRole(userRole); // Sử dụng phương thức setRole() của Account

    // 7. Kích hoạt tài khoản (Giả sử Account có trường 'enabled')
//    user.setEnabled(true); // Gọi trên đối tượng 'user'

    // 8. Lưu user (Account) vào CSDL
    return userRepository.save(user); // Sử dụng đúng biến 'user'
  }
}

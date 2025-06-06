package com.janet.basis.service;

import com.janet.basis.dto.UserRegistrationDto;
import com.janet.basis.entity.Account;

public interface UserService {

  Account registerNewUser(UserRegistrationDto registrationDto) throws Exception;
}

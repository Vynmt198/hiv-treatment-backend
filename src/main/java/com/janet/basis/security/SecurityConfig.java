package com.janet.basis.security;

// Import các class cần thiết một lần

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import nếu cần dùng cho HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // KHÔNG CẦN IMPORT NÀY NỮA

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private UserDetailsService userDetailsService;

  @Bean
  public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests((authorize) ->
            authorize
                .requestMatchers("/register/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login/**").permitAll()
                .requestMatchers("/users").hasRole("ADMIN") // Giả sử bạn muốn /users chỉ cho ADMIN
                // .requestMatchers(HttpMethod.GET, "/api/data").authenticated() // Ví dụ thêm
                .anyRequest().authenticated()
        ).formLogin(
            form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
        ).logout(
            logout -> logout
                // .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // Dòng cũ bị deprecated
                .logoutUrl("/logout") // <-- THAY ĐỔI Ở ĐÂY
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );
    return http.build();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
  }
}

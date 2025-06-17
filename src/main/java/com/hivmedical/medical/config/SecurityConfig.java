package com.hivmedical.medical.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
@Bean
  public BCryptPasswordEncoder passwordEncoder() {
  return new BCryptPasswordEncoder();
}
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web
        .ignoring()
        .requestMatchers("/**");
  }
//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http.authorizeHttpRequests(
//        authorizeRequests -> authorizeRequests.requestMatchers("/swagger-ui/**")
//            .permitAll()
//            .requestMatchers("/v3/api-docs*/**")
//            .permitAll());
//
//    return http.build();
//  }

//  ===============Thay đổi: Cho phép /api/auth/** không cần xác thực, tắt form login mặc định.
//@Bean
//public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//  http
//      .csrf(csrf -> csrf.disable())
//      .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//          .requestMatchers("/api/auth/**").permitAll()
//          .requestMatchers("/swagger-ui/**", "/v3/api-docs*/**").permitAll()
//          .anyRequest().authenticated()
//      )
//      .formLogin(form -> form.disable())
//      .httpBasic(httpBasic -> httpBasic.disable());
//  return http.build();
//}
}

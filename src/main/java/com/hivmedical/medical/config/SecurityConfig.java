package com.hivmedical.medical.config;

import com.hivmedical.medical.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
  private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(@Value("${jwt.secret}") String jwtSecret) {
    return new JwtAuthenticationFilter(jwtSecret);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    logger.info("Configuring SecurityFilterChain with rules");
    return http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> {
          logger.info("Setting up authorization rules");
          auth
              .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/doctors", "/api/doctors/**").permitAll()
              .requestMatchers(HttpMethod.POST, "/api/appointments").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/services", "/api/services/**").permitAll()
              .requestMatchers(HttpMethod.POST, "/api/services").hasRole("ADMIN")
              .requestMatchers(HttpMethod.PUT, "/api/services/**").hasRole("ADMIN")
              .requestMatchers(HttpMethod.DELETE, "/api/services/**").hasRole("ADMIN")
              .requestMatchers("/api/appointments/me", "/api/appointments/patient/**").hasRole("PATIENT")
              .requestMatchers("/api/admin/**").hasRole("ADMIN")
              .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
              .requestMatchers("/api/patient/**").hasRole("PATIENT")
              .anyRequest().authenticated();
        })
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exception -> exception
            .accessDeniedHandler((request, response, accessDeniedException) -> {
            ;  logger.error("Access denied to {}: {}", request.getRequestURI(), accessDeniedException.getMessage(), accessDeniedException);
              response.setContentType("application/json");
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              response.getWriter().write("{\"error\": \"Access denied\", \"details\": \"" + accessDeniedException.getMessage() + "\"}");
            })
            .authenticationEntryPoint((request, response, authException) -> {
              logger.error("Authentication failed for {}: {}", request.getRequestURI(), authException.getMessage(), authException);
              response.setContentType("application/json");
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.getWriter().write("{\"error\": \"Authentication failed\", \"details\": \"" + authException.getMessage() + "\"}");
            }))
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:5173");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    logger.info("Initializing AuthenticationManager");
    return authenticationConfiguration.getAuthenticationManager();
  }
}
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
import org.springframework.security.core.context.SecurityContextHolder;
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
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
      throws Exception {
    logger.info("Configuring SecurityFilterChain with rules");
    return http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> {
          logger.info("Setting up authorization rules for ARV treatment system");
          auth
              .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/doctors", "/api/doctors/**", "/api/blogs/**").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/services", "/api/services/**").permitAll()
              .requestMatchers(HttpMethod.POST, "/api/services").hasRole("ADMIN")
              .requestMatchers(HttpMethod.PUT, "/api/services/**").hasRole("ADMIN")
              .requestMatchers(HttpMethod.DELETE, "/api/services/**").hasRole("ADMIN")
              .requestMatchers(HttpMethod.POST, "/api/doctors", "/api/blogs").hasRole("ADMIN")
              .requestMatchers(HttpMethod.PUT, "/api/doctors/**", "/api/blogs/**").hasRole("ADMIN")
              .requestMatchers(HttpMethod.DELETE, "/api/doctors/**", "/api/blogs/**").hasRole("ADMIN")
              .requestMatchers(HttpMethod.GET, "/api/schedules", "/api/schedules/**").permitAll()
              .requestMatchers("/api/appointments/me", "/api/appointments/patient/**").hasRole("PATIENT")
              .requestMatchers(HttpMethod.POST, "/api/appointments").hasAnyRole("PATIENT", "ADMIN")
              .requestMatchers("/api/appointments/anonymous").permitAll()
              .requestMatchers("/api/appointments/anonymous-online").permitAll()
              .requestMatchers("/api/appointments/online").permitAll()
              .requestMatchers("/api/payment/momo").permitAll()
              .requestMatchers("/api/admin/**").hasRole("ADMIN")
              .requestMatchers("/api/admin/register-doctor").hasRole("ADMIN")
              .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
              .requestMatchers("/api/patient/**").hasRole("PATIENT")
              // ARV Protocol APIs - Public read access for specific endpoints
              .requestMatchers(HttpMethod.GET, "/api/arv-protocols/active", "/api/arv-protocols/target-groups",
                  "/api/arv-protocols/target-group/**", "/api/arv-protocols/search")
              .permitAll()
              // ARV Protocol APIs - Full access for ADMIN and DOCTOR
              .requestMatchers("/api/arv-protocols/**").hasAnyRole("ADMIN", "DOCTOR")
              // Prescription APIs - Full access for multiple roles
              .requestMatchers("/api/prescriptions/**").hasAnyRole("PATIENT", "DOCTOR", "STAFF", "ADMIN")
              // Medication APIs - Public read access for specific endpoints (must come first)
              .requestMatchers(HttpMethod.GET, "/api/medications/active", "/api/medications/drug-classes",
                  "/api/medications/drug-class/**", "/api/medications/search", "/api/medications/name/**",
                  "/api/medications/generic-name/**")
              .permitAll()
              // Medication APIs - Full access for ADMIN and DOCTOR (must come after specific
              // GET endpoints)
              .requestMatchers("/api/medications/**").hasAnyRole("ADMIN", "DOCTOR")
              .anyRequest().authenticated();
        })
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exception -> exception
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              logger.error("=== ACCESS DENIED DEBUG INFO ===");
              logger.error("Request URI: {}", request.getRequestURI());
              logger.error("Request Method: {}", request.getMethod());
              logger.error("Access Denied Exception: {}", accessDeniedException.getMessage());

              if (SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.error("User: {}", SecurityContextHolder.getContext().getAuthentication().getName());
                logger.error("User roles: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                logger.error("Is authenticated: {}",
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
              } else {
                logger.error("No authentication found in SecurityContext");
              }

              response.setContentType("application/json");
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              response.getWriter()
                  .write("{\"error\": \"Access denied\", \"details\": \"" + accessDeniedException.getMessage() + "\"}");
            })
            .authenticationEntryPoint((request, response, authException) -> {
              logger.error("Authentication failed for {}: {}", request.getRequestURI(), authException.getMessage(),
                  authException);
              response.setContentType("application/json");
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.getWriter()
                  .write("{\"error\": \"Authentication failed\", \"details\": \"" + authException.getMessage() + "\"}");
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
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    logger.info("Initializing AuthenticationManager");
    return authenticationConfiguration.getAuthenticationManager();
  }
}
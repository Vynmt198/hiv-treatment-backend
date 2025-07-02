package com.hivmedical.medical.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final String jwtSecret;

  public JwtAuthenticationFilter(@Value("${jwt.secret}") String jwtSecret) {
    this.jwtSecret = jwtSecret;
    logger.info("JwtAuthenticationFilter initialized with secret: {}", jwtSecret);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    String requestURI = request.getRequestURI();
    logger.info("Processing request to {} with Authorization header: {}", requestURI, header);

    if (header != null && header.startsWith("Bearer ")) {
      try {
        String token = header.substring(7);
        logger.debug("Verifying JWT token: {}", token);

        logger.debug("Using secret key: {}", jwtSecret);
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        JWTVerifier verifier = JWT.require(algorithm).build();

        logger.debug("JWT verifier initialized");
        DecodedJWT decodedJWT = verifier.verify(token);
        logger.debug("Token verification completed");

        String username = decodedJWT.getClaim("username").asString();
        String role = decodedJWT.getClaim("role").asString();
        logger.debug("Extracted claims - username: {}, role: {}", username, role);

        if (username == null || role == null) {

          logger.error("Invalid JWT token: missing username or role for request to {}", requestURI);
          response.setContentType("application/json");
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("{\"error\": \"Invalid JWT token: missing username or role\"}");
          return;
        }

        logger.info("Token verified for request to {} - username: {}, role: {}", requestURI, username, role);

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            username, null, Collections.singletonList(authority));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("Authentication set for user: {} with role: {} for request to {}", username, role, requestURI);
      } catch (JWTVerificationException e) {
        logger.error("JWT verification failed for request to {}: {}", requestURI, e.getMessage(), e);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter()
            .write("{\"error\": \"Invalid or expired token\", \"details\": \"" + e.getMessage() + "\"}");
        return;
      } catch (Exception e) {
        logger.error("Unexpected error during JWT processing for request to {}: {}", requestURI, e.getMessage(), e);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Unexpected error\", \"details\": \"" + e.getMessage() + "\"}");
        return;
      }
    } else {
      logger.debug("No Bearer token found in request to {}", requestURI);
    }
    logger.debug("Proceeding to next filter for request to {}", requestURI);
    chain.doFilter(request, response);
  }
}
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
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    logger.debug("Processing request with Authorization header: {}", header);

    if (header != null && header.startsWith("Bearer ")) {
      try {
        String token = header.substring(7);
        logger.debug("Verifying JWT token: {}", token);
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        String username = decodedJWT.getClaim("username").asString();
        String role = decodedJWT.getClaim("role").asString();
        if (username == null || role == null) {
          logger.error("Invalid JWT token: missing username or role");
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token: missing username or role");
          return;
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            username, null, Collections.singletonList(authority));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("Authentication set for user: {} with role: {}", username, role);
      } catch (JWTVerificationException e) {
        logger.error("JWT verification failed: {}", e.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token: " + e.getMessage());
        return;
      } catch (Exception e) {
        logger.error("Unexpected error during JWT processing: {}", e.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unexpected error: " + e.getMessage());
        return;
      }
    } else {
      logger.debug("No Bearer token found in request");
    }
    chain.doFilter(request, response);
  }
}
package io.spring.api.security;

import io.spring.core.service.JwtService;
import io.spring.core.user.UserRepository;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class JwtTokenFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
  @Autowired private UserRepository userRepository;
  @Autowired private JwtService jwtService;
  private final String header = "Authorization";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    logger.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());
    String authHeader = request.getHeader(header);
    logger.debug("Authorization header: {}", authHeader != null ? "present" : "not present");

    Optional<String> tokenOpt = getTokenString(authHeader);
    logger.debug(
        "Extracted token: {}",
        tokenOpt.isPresent() ? "present (length=" + tokenOpt.get().length() + ")" : "not present");

    tokenOpt
        .flatMap(
            token -> {
              Optional<String> subOpt = jwtService.getSubFromToken(token);
              logger.debug(
                  "Token validation result: {}",
                  subOpt.isPresent() ? "valid, userId=" + subOpt.get() : "invalid or expired");
              return subOpt;
            })
        .ifPresent(
            id -> {
              if (SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.debug("Looking up user with id: {}", id);
                userRepository
                    .findById(id)
                    .ifPresent(
                        user -> {
                          logger.debug(
                              "User found: {}, setting authentication", user.getUsername());
                          UsernamePasswordAuthenticationToken authenticationToken =
                              new UsernamePasswordAuthenticationToken(
                                  user, null, Collections.emptyList());
                          authenticationToken.setDetails(
                              new WebAuthenticationDetailsSource().buildDetails(request));
                          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                          logger.debug(
                              "Authentication set successfully for user: {}", user.getUsername());
                        });
                if (!userRepository.findById(id).isPresent()) {
                  logger.warn("User not found in database for id: {}", id);
                }
              } else {
                logger.debug("Authentication already set, skipping");
              }
            });

    filterChain.doFilter(request, response);
  }

  private Optional<String> getTokenString(String header) {
    if (header == null) {
      return Optional.empty();
    } else {
      String[] split = header.split(" ");
      if (split.length < 2) {
        return Optional.empty();
      } else {
        return Optional.ofNullable(split[1]);
      }
    }
  }
}

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
  private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);
  @Autowired private UserRepository userRepository;
  @Autowired private JwtService jwtService;
  private final String header = "Authorization";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    log.debug(
        "Processing authentication for request: {} {}",
        request.getMethod(),
        request.getRequestURI());

    Optional<String> tokenString = getTokenString(request.getHeader(header));
    if (tokenString.isEmpty()) {
      log.debug("No token found in Authorization header");
      filterChain.doFilter(request, response);
      return;
    }

    log.debug("Token found in Authorization header");

    tokenString
        .flatMap(
            token -> {
              Optional<String> userId = jwtService.getSubFromToken(token);
              if (userId.isEmpty()) {
                log.debug("Failed to extract user ID from token");
              }
              return userId;
            })
        .ifPresent(
            id -> {
              log.debug("Extracted user ID from token: {}", id);
              if (SecurityContextHolder.getContext().getAuthentication() == null) {
                userRepository
                    .findById(id)
                    .ifPresent(
                        user -> {
                          log.debug("User found in database: {}", user.getUsername());
                          UsernamePasswordAuthenticationToken authenticationToken =
                              new UsernamePasswordAuthenticationToken(
                                  user, null, Collections.emptyList());
                          authenticationToken.setDetails(
                              new WebAuthenticationDetailsSource().buildDetails(request));
                          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                          log.debug("SecurityContext set for user: {}", user.getUsername());
                        });
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                  log.debug("User not found in database for ID: {}", id);
                }
              } else {
                log.debug("SecurityContext already has authentication");
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

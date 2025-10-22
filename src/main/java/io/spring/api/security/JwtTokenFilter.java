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
    String authHeader = request.getHeader(header);
    log.debug(
        "Processing request with Authorization header: {}",
        authHeader != null ? "present" : "absent");

    Optional<String> tokenOpt = getTokenString(authHeader);
    log.debug("Token extraction result: {}", tokenOpt.isPresent() ? "token found" : "no token");

    tokenOpt
        .flatMap(
            token -> {
              Optional<String> subOpt = jwtService.getSubFromToken(token);
              log.debug(
                  "JWT subject extraction result: {}",
                  subOpt.isPresent() ? "user ID found: " + subOpt.get() : "no user ID");
              return subOpt;
            })
        .ifPresent(
            id -> {
              if (SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("No existing authentication, fetching user from database for ID: {}", id);
                userRepository
                    .findById(id)
                    .ifPresent(
                        user -> {
                          log.debug(
                              "User found in database: {}, setting authentication",
                              user.getUsername());
                          UsernamePasswordAuthenticationToken authenticationToken =
                              new UsernamePasswordAuthenticationToken(
                                  user, null, Collections.emptyList());
                          authenticationToken.setDetails(
                              new WebAuthenticationDetailsSource().buildDetails(request));
                          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                          log.debug(
                              "Authentication successfully set in SecurityContext for user: {}",
                              user.getUsername());
                        });
                if (!userRepository.findById(id).isPresent()) {
                  log.warn("User not found in database for ID: {}", id);
                }
              } else {
                log.debug("Authentication already exists in SecurityContext, skipping");
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

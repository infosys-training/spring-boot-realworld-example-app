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
    if (authHeader == null) {
      logger.debug("No Authorization header found in request");
    } else {
      logger.debug("Authorization header present");
    }

    Optional<String> tokenString = getTokenString(authHeader);
    if (tokenString.isPresent()) {
      logger.debug("Token extracted from Authorization header");
      Optional<String> userIdOpt = jwtService.getSubFromToken(tokenString.get());
      if (userIdOpt.isPresent()) {
        String userId = userIdOpt.get();
        logger.debug("Token validation successful, extracted user ID: {}", userId);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
          logger.debug("No existing authentication, looking up user in database");
          userRepository
              .findById(userId)
              .ifPresentOrElse(
                  user -> {
                    logger.info(
                        "User found in database, setting authentication for username: {}",
                        user.getUsername());
                    UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                            user, null, Collections.emptyList());
                    authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                  },
                  () -> logger.warn("User ID {} from valid token not found in database", userId));
        } else {
          logger.debug("User already authenticated, skipping authentication setup");
        }
      } else {
        logger.debug("Token validation failed");
      }
    } else {
      logger.debug("Failed to extract token from Authorization header");
    }

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

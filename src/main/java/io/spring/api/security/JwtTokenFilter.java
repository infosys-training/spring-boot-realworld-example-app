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
    String authHeader = request.getHeader(header);
    logger.debug(
        "Processing request to: {} with Authorization header: {}",
        request.getRequestURI(),
        authHeader != null ? "present (length: " + authHeader.length() + ")" : "absent");

    Optional<String> tokenOpt = getTokenString(authHeader);
    if (tokenOpt.isPresent()) {
      logger.debug("Successfully extracted token from Authorization header");

      Optional<String> userIdOpt = jwtService.getSubFromToken(tokenOpt.get());
      if (userIdOpt.isPresent()) {
        String userId = userIdOpt.get();
        logger.debug("Successfully extracted user ID from token: {}", userId);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
          Optional<io.spring.core.user.User> userOpt = userRepository.findById(userId);
          if (userOpt.isPresent()) {
            logger.debug("Successfully found user in database: {}", userId);
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    userOpt.get(), null, Collections.emptyList());
            authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.info("Authentication successful for user: {}", userId);
          } else {
            logger.warn("User ID extracted from token but not found in database: {}", userId);
          }
        } else {
          logger.debug("Authentication already exists in SecurityContext");
        }
      } else {
        logger.warn("Failed to extract user ID from token - token validation failed");
      }
    } else {
      logger.debug("No valid token extracted from Authorization header");
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

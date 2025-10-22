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
    logger.debug("Authorization header: {}", authHeader);

    Optional<String> tokenOpt = getTokenString(authHeader);
    if (tokenOpt.isPresent()) {
      String token = tokenOpt.get();
      logger.debug("Extracted token: {}", token);

      Optional<String> userIdOpt = jwtService.getSubFromToken(token);
      if (userIdOpt.isPresent()) {
        String userId = userIdOpt.get();
        logger.debug("User ID extracted from token: {}", userId);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
          userRepository
              .findById(userId)
              .ifPresent(
                  user -> {
                    UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                            user, null, Collections.emptyList());
                    authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.debug(
                        "Authentication set successfully for user: {}", user.getUsername());
                  });

          if (SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("User not found in database for ID: {}", userId);
          }
        } else {
          logger.debug("Authentication already set, skipping");
        }
      } else {
        logger.debug("Failed to extract user ID from token");
      }
    } else {
      logger.debug("No token found in Authorization header");
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

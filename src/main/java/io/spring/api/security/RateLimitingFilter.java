package io.spring.api.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

public class RateLimitingFilter extends OncePerRequestFilter {

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    String method = request.getMethod();

    if (isRateLimited(path, method)) {
      String clientIp = getClientIp(request);
      Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createBucket());
      if (bucket.tryConsume(1)) {
        filterChain.doFilter(request, response);
      } else {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response
            .getWriter()
            .write("{\"errors\":{\"body\":[\"Too many login attempts. Try again later.\"]}}");
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }

  private boolean isRateLimited(String path, String method) {
    return ("POST".equalsIgnoreCase(method) && "/users/login".equals(path))
        || ("POST".equalsIgnoreCase(method) && "/graphql".equals(path));
  }

  private Bucket createBucket() {
    Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
    return Bucket.builder().addLimit(limit).build();
  }

  private String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}

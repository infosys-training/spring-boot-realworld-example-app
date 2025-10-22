package io.spring.infrastructure.service;

import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJwtServiceTest {
  private static final Logger logger = LoggerFactory.getLogger(DefaultJwtServiceTest.class);

  private JwtService jwtService;

  @BeforeEach
  public void setUp() {
    jwtService =
        new DefaultJwtService("123123123123123123123123123123123123123123123123123123123123", 3600);
  }

  @Test
  public void should_generate_and_parse_token() {
    User user = new User("email@email.com", "username", "123", "", "");
    String token = jwtService.toToken(user);
    Assertions.assertNotNull(token);
    Optional<String> optional = jwtService.getSubFromToken(token);
    Assertions.assertTrue(optional.isPresent());
    Assertions.assertEquals(optional.get(), user.getId());
  }

  @Test
  public void should_get_null_with_wrong_jwt() {
    Optional<String> optional = jwtService.getSubFromToken("123");
    Assertions.assertFalse(optional.isPresent());
  }

  @Test
  public void should_get_null_with_expired_jwt() {
    String token =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhaXNlbnNpeSIsImV4cCI6MTUwMjE2MTIwNH0.SJB-U60WzxLYNomqLo4G3v3LzFxJKuVrIud8D8Lz3-mgpo9pN1i7C8ikU_jQPJGm8HsC1CquGMI-rSuM7j6LDA";
    Assertions.assertFalse(jwtService.getSubFromToken(token).isPresent());
  }

  @Test
  public void diagnostic_validate_real_token() {
    logger.info("=== JWT Diagnostic Test Started ===");

    User testUser = new User("test@example.com", "testuser", "123", "", "");
    logger.info("Creating token for user: {}", testUser.getUsername());

    String generatedToken = jwtService.toToken(testUser);
    logger.info(
        "Generated token (first 50 chars): {}",
        generatedToken.length() > 50 ? generatedToken.substring(0, 50) + "..." : generatedToken);

    Optional<String> validationResult = jwtService.getSubFromToken(generatedToken);
    if (validationResult.isPresent()) {
      logger.info("Token validation SUCCESS - User ID: {}", validationResult.get());
      Assertions.assertEquals(testUser.getId(), validationResult.get());
    } else {
      logger.error("Token validation FAILED - Check DefaultJwtService logs for exception details");
      Assertions.fail("Token validation failed - should have been valid");
    }

    logger.info("Testing with production token from failing deployment:");
    logger.info(
        "To diagnose production issues, replace the empty string below with the actual JWT token");
    logger.info("from the Authorization header of a failed request, then re-run this test.");

    String productionToken = "";
    if (!productionToken.isEmpty()) {
      logger.info("Validating production token...");
      Optional<String> prodResult = jwtService.getSubFromToken(productionToken);
      if (prodResult.isPresent()) {
        logger.info("Production token validation SUCCESS - User ID: {}", prodResult.get());
      } else {
        logger.error(
            "Production token validation FAILED - Check logs above for specific exception");
      }
    }

    logger.info("=== JWT Diagnostic Test Completed ===");
  }
}

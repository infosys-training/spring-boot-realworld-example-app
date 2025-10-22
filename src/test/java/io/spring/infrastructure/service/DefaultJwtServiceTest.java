package io.spring.infrastructure.service;

import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultJwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  public void setUp() {
    jwtService =
        new DefaultJwtService("123123123123123123123123123123123123123123123123123123123123", "", 3600);
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
  public void should_validate_token_with_old_secret() {
    String oldSecret = "TEST-OLD-SECRET-ABCD1234-EFGH5678-IJKL9012-MNOP3456-QRST7890-UVWX";
    String newSecret = "TEST-NEW-SECRET-ZYXW0987-VUTR6543-SRQP2109-NMLK8765-JIHG4321-FEDC";
    
    User user = new User("test@test.com", "testuser", "456", "", "");
    
    JwtService oldJwtService = new DefaultJwtService(oldSecret, "", 3600);
    String tokenWithOldSecret = oldJwtService.toToken(user);
    
    JwtService newJwtServiceWithOldFallback = new DefaultJwtService(newSecret, oldSecret, 3600);
    Optional<String> result = newJwtServiceWithOldFallback.getSubFromToken(tokenWithOldSecret);
    
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(user.getId(), result.get());
  }

  @Test
  public void should_reject_token_when_old_secret_not_configured() {
    String oldSecret = "TEST-OLD-SECRET-ABCD1234-EFGH5678-IJKL9012-MNOP3456-QRST7890-UVWX";
    String newSecret = "TEST-NEW-SECRET-ZYXW0987-VUTR6543-SRQP2109-NMLK8765-JIHG4321-FEDC";
    
    User user = new User("test@test.com", "testuser", "789", "", "");
    
    JwtService oldJwtService = new DefaultJwtService(oldSecret, "", 3600);
    String tokenWithOldSecret = oldJwtService.toToken(user);
    
    JwtService newJwtServiceWithoutOldFallback = new DefaultJwtService(newSecret, "", 3600);
    Optional<String> result = newJwtServiceWithoutOldFallback.getSubFromToken(tokenWithOldSecret);
    
    Assertions.assertFalse(result.isPresent());
  }
}

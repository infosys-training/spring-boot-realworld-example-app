package io.spring.api.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakJwtAuthenticationConverterTest {

  @Mock private UserRepository userRepository;

  private KeycloakJwtAuthenticationConverter converter;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    converter = new KeycloakJwtAuthenticationConverter(userRepository);
  }

  @Test
  public void should_convert_jwt_with_email_claim() {
    User user = new User("test@example.com", "testuser", "password", "", "");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    Map<String, Object> claims = new HashMap<>();
    claims.put("email", "test@example.com");
    Jwt jwt = createJwt(claims);

    AbstractAuthenticationToken auth = converter.convert(jwt);

    assertNotNull(auth);
    assertEquals(user, auth.getPrincipal());
    verify(userRepository).findByEmail("test@example.com");
  }

  @Test
  public void should_convert_jwt_with_username_claim() {
    User user = new User("test@example.com", "testuser", "password", "", "");
    when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    Map<String, Object> claims = new HashMap<>();
    claims.put("preferred_username", "testuser");
    Jwt jwt = createJwt(claims);

    AbstractAuthenticationToken auth = converter.convert(jwt);

    assertNotNull(auth);
    assertEquals(user, auth.getPrincipal());
    verify(userRepository).findByUsername("testuser");
  }

  @Test
  public void should_return_null_when_user_not_found() {
    when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

    Map<String, Object> claims = new HashMap<>();
    claims.put("email", "unknown@example.com");
    Jwt jwt = createJwt(claims);

    AbstractAuthenticationToken auth = converter.convert(jwt);

    assertNull(auth);
  }

  @Test
  public void should_return_null_when_no_email_or_username_claims() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", "some-subject");
    Jwt jwt = createJwt(claims);

    AbstractAuthenticationToken auth = converter.convert(jwt);

    assertNull(auth);
  }

  @Test
  public void should_prefer_email_over_username() {
    User userByEmail = new User("test@example.com", "testuser", "password", "", "");
    User userByUsername = new User("other@example.com", "testuser", "password", "", "");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userByEmail));

    Map<String, Object> claims = new HashMap<>();
    claims.put("email", "test@example.com");
    claims.put("preferred_username", "testuser");
    Jwt jwt = createJwt(claims);

    AbstractAuthenticationToken auth = converter.convert(jwt);

    assertNotNull(auth);
    assertEquals(userByEmail, auth.getPrincipal());
    verify(userRepository).findByEmail("test@example.com");
    verify(userRepository, never()).findByUsername(anyString());
  }

  private Jwt createJwt(Map<String, Object> claims) {
    return new Jwt(
        "token", Instant.now(), Instant.now().plusSeconds(3600), Map.of("alg", "RS256"), claims);
  }
}

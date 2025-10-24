package io.spring.api.security;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Collections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "spring.security.oauth2.resourceserver.jwt.issuer-uri")
public class KeycloakJwtAuthenticationConverter
    implements Converter<Jwt, AbstractAuthenticationToken> {

  private final UserRepository userRepository;

  public KeycloakJwtAuthenticationConverter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    String email = jwt.getClaimAsString("email");
    String username = jwt.getClaimAsString("preferred_username");

    if (email == null && username == null) {
      return null;
    }

    User user = null;
    if (email != null) {
      user = userRepository.findByEmail(email).orElse(null);
    }
    if (user == null && username != null) {
      user = userRepository.findByUsername(username).orElse(null);
    }

    if (user == null) {
      return null;
    }

    return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
  }
}

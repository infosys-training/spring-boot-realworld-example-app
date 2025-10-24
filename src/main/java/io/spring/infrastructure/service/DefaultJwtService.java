package io.spring.infrastructure.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DefaultJwtService implements JwtService {
  private static final Logger logger = LoggerFactory.getLogger(DefaultJwtService.class);
  private final SecretKey signingKey;
  private final SignatureAlgorithm signatureAlgorithm;
  private int sessionTime;

  @Autowired
  public DefaultJwtService(
      @Value("${jwt.secret}") String secret, @Value("${jwt.sessionTime}") int sessionTime) {
    this.sessionTime = sessionTime;
    signatureAlgorithm = SignatureAlgorithm.HS512;
    this.signingKey = new SecretKeySpec(secret.getBytes(), signatureAlgorithm.getJcaName());
  }

  @Override
  public String toToken(User user) {
    return Jwts.builder()
        .setSubject(user.getId())
        .setExpiration(expireTimeFromNow())
        .signWith(signingKey)
        .compact();
  }

  @Override
  public Optional<String> getSubFromToken(String token) {
    logger.debug("Starting JWT token parsing");
    try {
      Jws<Claims> claimsJws =
          Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
      String subject = claimsJws.getBody().getSubject();
      Date expiration = claimsJws.getBody().getExpiration();
      logger.debug(
          "JWT token parsed successfully - Subject: {}, Expiration: {}", subject, expiration);
      return Optional.ofNullable(subject);
    } catch (ExpiredJwtException e) {
      logger.warn("JWT token has expired: {}", e.getMessage());
      return Optional.empty();
    } catch (SignatureException e) {
      logger.error(
          "JWT token signature verification failed - invalid signature: {}", e.getMessage());
      return Optional.empty();
    } catch (MalformedJwtException e) {
      logger.error("Malformed JWT token: {}", e.getMessage());
      return Optional.empty();
    } catch (Exception e) {
      logger.error("Unexpected error parsing JWT token", e);
      return Optional.empty();
    }
  }

  private Date expireTimeFromNow() {
    return new Date(System.currentTimeMillis() + sessionTime * 1000L);
  }
}

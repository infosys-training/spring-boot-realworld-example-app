package io.spring.api;

import io.spring.core.service.JwtService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/debug")
public class DebugApi {

  @Autowired private JwtService jwtService;

  @Value("${jwt.sessionTime}")
  private int sessionTime;

  private final SecretKey signingKey;
  private final String signatureAlgorithm;

  @Autowired
  public DebugApi(JwtService jwtService, @Value("${jwt.secret}") String secret) {
    this.jwtService = jwtService;
    this.signingKey = new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA512");
    this.signatureAlgorithm = "HS512";
  }

  @GetMapping("/validate-token")
  public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String auth) {
    String token = auth.split(" ")[1];
    Optional<String> userId = jwtService.getSubFromToken(token);
    Map<String, Object> response = new HashMap<>();
    response.put("valid", userId.isPresent());
    response.put("userId", userId.orElse("INVALID"));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/jwt-config")
  public ResponseEntity<?> getJwtConfig() {
    Map<String, Object> response = new HashMap<>();
    response.put("secretLength", signingKey.getEncoded().length);
    response.put("algorithm", signatureAlgorithm);
    response.put("sessionTime", sessionTime);
    return ResponseEntity.ok(response);
  }
}

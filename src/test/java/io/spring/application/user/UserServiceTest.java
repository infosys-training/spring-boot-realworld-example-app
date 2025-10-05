package io.spring.application.user;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisUserRepository;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Import({
  UserService.class,
  MyBatisUserRepository.class,
  UserServiceTest.TestConfig.class
})
public class UserServiceTest extends DbTestBase {

  @Autowired private UserService userService;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private User existingUser;

  @TestConfiguration
  static class TestConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }

    @Bean
    public String defaultImage() {
      return "https://static.productionready.io/images/smiley-cyrus.jpg";
    }
  }

  @BeforeEach
  public void setUp() {
    existingUser = new User("existing@example.com", "existinguser", "encodedpass", "Existing Bio", "https://example.com/existing.jpg");
    userRepository.save(existingUser);
  }

  @Test
  public void should_create_user_successfully() {
    RegisterParam param = new RegisterParam("newuser@example.com", "newusername", "password123");

    User user = userService.createUser(param);

    Assertions.assertNotNull(user);
    Assertions.assertNotNull(user.getId());
    Assertions.assertEquals("newuser@example.com", user.getEmail());
    Assertions.assertEquals("newusername", user.getUsername());
    Assertions.assertNotEquals("password123", user.getPassword());
    Assertions.assertTrue(passwordEncoder.matches("password123", user.getPassword()));
    Assertions.assertEquals("", user.getBio());
    Assertions.assertEquals("https://static.productionready.io/images/smiley-cyrus.jpg", user.getImage());

    Optional<User> saved = userRepository.findById(user.getId());
    Assertions.assertTrue(saved.isPresent());
    Assertions.assertEquals(user.getId(), saved.get().getId());
  }

  @Test
  public void should_encode_password_when_creating_user() {
    String rawPassword = "mySecretPassword";
    RegisterParam param = new RegisterParam("test@example.com", "testuser", rawPassword);

    User user = userService.createUser(param);

    Assertions.assertNotEquals(rawPassword, user.getPassword());
    Assertions.assertTrue(passwordEncoder.matches(rawPassword, user.getPassword()));
  }

  @Test
  public void should_use_default_image_when_creating_user() {
    RegisterParam param = new RegisterParam("test@example.com", "testuser", "password");

    User user = userService.createUser(param);

    Assertions.assertEquals("https://static.productionready.io/images/smiley-cyrus.jpg", user.getImage());
  }

  @Test
  public void should_update_user_email() {
    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email("newemail@example.com")
        .username("")
        .password("")
        .bio("")
        .image("")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(existingUser, updateParam);

    userService.updateUser(command);

    Assertions.assertEquals("newemail@example.com", existingUser.getEmail());
    Assertions.assertEquals("existinguser", existingUser.getUsername());
  }

  @Test
  public void should_update_user_username() {
    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email("")
        .username("newusername")
        .password("")
        .bio("")
        .image("")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(existingUser, updateParam);

    userService.updateUser(command);

    Assertions.assertEquals("existing@example.com", existingUser.getEmail());
    Assertions.assertEquals("newusername", existingUser.getUsername());
  }

  @Test
  public void should_update_user_password() {
    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email("")
        .username("")
        .password("newpassword")
        .bio("")
        .image("")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(existingUser, updateParam);

    userService.updateUser(command);

    Assertions.assertEquals("newpassword", existingUser.getPassword());
  }

  @Test
  public void should_update_user_bio() {
    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email("")
        .username("")
        .password("")
        .bio("Updated bio")
        .image("")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(existingUser, updateParam);

    userService.updateUser(command);

    Assertions.assertEquals("Updated bio", existingUser.getBio());
  }

  @Test
  public void should_update_user_image() {
    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email("")
        .username("")
        .password("")
        .bio("")
        .image("https://example.com/newimage.jpg")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(existingUser, updateParam);

    userService.updateUser(command);

    Assertions.assertEquals("https://example.com/newimage.jpg", existingUser.getImage());
  }

  @Test
  public void should_update_multiple_user_fields() {
    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email("updated@example.com")
        .username("updateduser")
        .password("newpass")
        .bio("New bio")
        .image("https://example.com/new.jpg")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(existingUser, updateParam);

    userService.updateUser(command);

    Assertions.assertEquals("updated@example.com", existingUser.getEmail());
    Assertions.assertEquals("updateduser", existingUser.getUsername());
    Assertions.assertEquals("newpass", existingUser.getPassword());
    Assertions.assertEquals("New bio", existingUser.getBio());
    Assertions.assertEquals("https://example.com/new.jpg", existingUser.getImage());
  }

  @Test
  public void should_not_update_fields_when_empty_string() {
    String originalEmail = existingUser.getEmail();
    String originalUsername = existingUser.getUsername();
    String originalPassword = existingUser.getPassword();
    String originalBio = existingUser.getBio();
    String originalImage = existingUser.getImage();

    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email("")
        .username("")
        .password("")
        .bio("")
        .image("")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(existingUser, updateParam);

    userService.updateUser(command);

    Assertions.assertEquals(originalEmail, existingUser.getEmail());
    Assertions.assertEquals(originalUsername, existingUser.getUsername());
    Assertions.assertEquals(originalPassword, existingUser.getPassword());
    Assertions.assertEquals(originalBio, existingUser.getBio());
    Assertions.assertEquals(originalImage, existingUser.getImage());
  }

  @Test
  public void should_persist_user_updates_to_repository() {
    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email("persisted@example.com")
        .username("")
        .password("")
        .bio("")
        .image("")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(existingUser, updateParam);

    userService.updateUser(command);

    Optional<User> retrieved = userRepository.findById(existingUser.getId());
    Assertions.assertTrue(retrieved.isPresent());
    Assertions.assertEquals("persisted@example.com", retrieved.get().getEmail());
  }

  @Test
  public void should_allow_update_with_same_email() {
    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email(existingUser.getEmail())
        .username("newusername")
        .password("")
        .bio("")
        .image("")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(existingUser, updateParam);

    Assertions.assertDoesNotThrow(() -> userService.updateUser(command));
  }

  @Test
  public void should_allow_update_with_same_username() {
    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email("newemail@example.com")
        .username(existingUser.getUsername())
        .password("")
        .bio("")
        .image("")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(existingUser, updateParam);

    Assertions.assertDoesNotThrow(() -> userService.updateUser(command));
  }

}

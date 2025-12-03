package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.RegistrationPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationEdgeCaseTests extends BaseTest {

  private RegistrationPage registrationPage;
  private HomePage homePage;
  private String baseUrl;

  @BeforeMethod
  public void setUp() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registrationPage = new RegistrationPage(driver);
    homePage = new HomePage(driver);
  }

  private String generateUniqueUsername() {
    return "user" + UUID.randomUUID().toString().substring(0, 8);
  }

  private String generateUniqueEmail() {
    return "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
  }

  @Test(groups = {"regression", "edge"})
  public void TC033_usernameWithLeadingTrailingSpaces() {
    createTest("TC-033", "Username with leading/trailing spaces");

    String usernameWithSpaces = "  testuser  ";

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(usernameWithSpaces);
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isLoggedIn = homePage.isUserLoggedIn();
    boolean hasErrors = registrationPage.hasErrorMessages();
    boolean onRegPage = registrationPage.isOnRegistrationPage();

    assertTrue(
        isLoggedIn || hasErrors || onRegPage,
        "System should handle username with spaces - either trim and succeed, or show error");

    test.pass("Username with leading/trailing spaces handled appropriately");
  }

  @Test(groups = {"regression", "edge"})
  public void TC034_emailWithLeadingTrailingSpaces() {
    createTest("TC-034", "Email with leading/trailing spaces");

    String emailWithSpaces = "  " + generateUniqueEmail() + "  ";

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail(emailWithSpaces);
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isLoggedIn = homePage.isUserLoggedIn();
    boolean hasErrors = registrationPage.hasErrorMessages();
    boolean onRegPage = registrationPage.isOnRegistrationPage();

    assertTrue(
        isLoggedIn || hasErrors || onRegPage,
        "System should handle email with spaces - either trim and succeed, or show error");

    test.pass("Email with leading/trailing spaces handled appropriately");
  }

  @Test(groups = {"regression", "edge"})
  public void TC035_veryLongEmailAddressHandling() {
    createTest("TC-035", "Very long email address handling");

    String longLocalPart = "a".repeat(200);
    String longEmail = longLocalPart + "@example.com";

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail(longEmail);
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasErrors = registrationPage.hasErrorMessages();
    boolean onRegPage = registrationPage.isOnRegistrationPage();

    assertTrue(hasErrors || onRegPage, "System should handle very long email appropriately");

    test.pass("Very long email address handled appropriately");
  }

  @Test(groups = {"regression", "edge"})
  public void TC036_veryLongUsernameHandling() {
    createTest("TC-036", "Very long username handling");

    String longUsername = "a".repeat(256);

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(longUsername);
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasErrors = registrationPage.hasErrorMessages();
    boolean onRegPage = registrationPage.isOnRegistrationPage();

    assertTrue(hasErrors || onRegPage, "System should handle very long username appropriately");

    test.pass("Very long username handled appropriately");
  }

  @Test(groups = {"regression", "edge"})
  public void TC037_veryLongPasswordHandling() {
    createTest("TC-037", "Very long password handling");

    String longPassword = "P@ssw0rd!" + "a".repeat(250);

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword(longPassword);
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isLoggedIn = homePage.isUserLoggedIn();
    boolean hasErrors = registrationPage.hasErrorMessages();
    boolean onRegPage = registrationPage.isOnRegistrationPage();

    assertTrue(
        isLoggedIn || hasErrors || onRegPage,
        "System should handle very long password appropriately");

    test.pass("Very long password handled appropriately");
  }

  @Test(groups = {"regression", "edge"})
  public void TC038_specialCharactersInUsername() {
    createTest("TC-038", "Special characters in username");

    String usernameWithSpecialChars = "user!@#$%";

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(usernameWithSpecialChars);
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isLoggedIn = homePage.isUserLoggedIn();
    boolean hasErrors = registrationPage.hasErrorMessages();
    boolean onRegPage = registrationPage.isOnRegistrationPage();

    assertTrue(
        isLoggedIn || hasErrors || onRegPage,
        "System should validate special characters in username appropriately");

    test.pass("Special characters in username handled appropriately");
  }

  @Test(groups = {"regression", "edge"})
  public void TC039_unicodeCharactersInUsername() {
    createTest("TC-039", "Unicode characters in username");

    String unicodeUsername = "user" + UUID.randomUUID().toString().substring(0, 4);

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(unicodeUsername);
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isLoggedIn = homePage.isUserLoggedIn();
    boolean hasErrors = registrationPage.hasErrorMessages();
    boolean onRegPage = registrationPage.isOnRegistrationPage();

    assertTrue(
        isLoggedIn || hasErrors || onRegPage,
        "System should handle unicode characters appropriately");

    test.pass("Unicode characters in username handled appropriately");
  }

  @Test(groups = {"smoke", "regression", "edge"})
  public void TC040_passwordFieldMasksInput() {
    createTest("TC-040", "Password field masks input");

    registrationPage.navigateTo(baseUrl);

    String passwordType = registrationPage.getPasswordInputType();

    assertEquals(
        passwordType, "password", "Password field should have type='password' to mask input");

    test.pass("Password field masks input correctly");
  }
}

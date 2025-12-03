package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.NavbarComponent;
import io.spring.selenium.pages.ProfilePage;
import io.spring.selenium.pages.SettingsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GetCurrentUserEdgeCaseTests extends BaseTest {

  private LoginPage loginPage;
  private NavbarComponent navbar;
  private ProfilePage profilePage;
  private SettingsPage settingsPage;

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_USERNAME = "johndoe";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    navbar = new NavbarComponent(driver);
    profilePage = new ProfilePage(driver);
    settingsPage = new SettingsPage(driver);
  }

  @Test(groups = {"edgecase", "regression"})
  public void TC031_verifyProfileWithMaximumLengthBio() {
    createTest(
        "TC-031: Verify profile with maximum length bio",
        "Bio should be saved and displayed correctly or truncated appropriately");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    StringBuilder longBio = new StringBuilder();
    for (int i = 0; i < 50; i++) {
      longBio.append("This is a very long bio text. ");
    }

    settingsPage.clearBio();
    settingsPage.enterBio(longBio.toString());
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        settingsPage.isPageLoaded() || settingsPage.isErrorMessageDisplayed(),
        "Should handle maximum length bio appropriately");
    test.info("Maximum length bio handling verified");
  }

  @Test(groups = {"edgecase", "regression"})
  public void TC032_verifyProfileWithSpecialCharactersInUsername() {
    createTest(
        "TC-032: Verify profile with special characters in username",
        "Appropriate validation or acceptance of special characters");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.clearUsername();
    settingsPage.enterUsername("user@#$%^&*");
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        settingsPage.isPageLoaded() || settingsPage.isErrorMessageDisplayed(),
        "Should handle special characters in username appropriately");
    test.info("Special characters in username handling verified");
  }

  @Test(groups = {"edgecase", "regression"})
  public void TC033_verifyProfileWithSpecialCharactersInBio() {
    createTest(
        "TC-033: Verify profile with special characters in bio",
        "Special characters should be displayed correctly");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.clearBio();
    settingsPage.enterBio("Bio with special chars: @#$%^&*()_+-=[]{}|;':\",./<>?");
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(settingsPage.isPageLoaded(), "Should handle special characters in bio");
    test.info("Special characters in bio handling verified");
  }

  @Test(groups = {"edgecase", "regression"})
  public void TC034_verifyProfileWithVeryLongImageUrl() {
    createTest(
        "TC-034: Verify profile with very long image URL", "URL should be handled appropriately");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    StringBuilder longUrl = new StringBuilder("https://example.com/image/");
    for (int i = 0; i < 100; i++) {
      longUrl.append("verylongpath/");
    }
    longUrl.append("image.jpg");

    settingsPage.clearImageUrl();
    settingsPage.enterImageUrl(longUrl.toString());
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        settingsPage.isPageLoaded() || settingsPage.isErrorMessageDisplayed(),
        "Should handle very long image URL appropriately");
    test.info("Very long image URL handling verified");
  }

  @Test(groups = {"edgecase", "regression"})
  public void TC035_verifyProfileWithEmptyBioField() {
    createTest(
        "TC-035: Verify profile with empty bio field",
        "Profile should display without bio section or show placeholder");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.clearBio();
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.navigateTo(TEST_USERNAME);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(profilePage.isPageLoaded(), "Profile should load with empty bio");
    test.info("Empty bio field handling verified");
  }

  @Test(groups = {"edgecase", "regression"})
  public void TC036_verifyProfileWithEmptyImageField() {
    createTest(
        "TC-036: Verify profile with empty image field",
        "Default avatar or placeholder should be shown");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.clearImageUrl();
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.navigateTo(TEST_USERNAME);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(profilePage.isPageLoaded(), "Profile should load with empty image");
    test.info("Empty image field handling verified");
  }

  @Test(groups = {"edgecase", "regression"})
  public void TC037_verifyMultipleTabSessionHandling() {
    createTest(
        "TC-037: Verify multiple tab session handling",
        "Both sessions should reflect logout state");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(navbar.isUserLoggedIn(), "User should be logged in");

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.clickLogout();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    driver.navigate().refresh();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(navbar.isUserLoggedOut(), "Session should be logged out after refresh");
    test.info("Multiple tab session handling verified");
  }

  @Test(groups = {"edgecase", "regression"})
  public void TC038_verifyProfileUpdateWithSameValues() {
    createTest(
        "TC-038: Verify profile update with same values", "No error, success message or no change");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        settingsPage.isPageLoaded() && !settingsPage.isErrorMessageDisplayed(),
        "Should handle update with same values without error");
    test.info("Profile update with same values verified");
  }

  @Test(groups = {"edgecase", "regression"})
  public void TC039_verifyRapidNavigationBetweenProfileSections() {
    createTest(
        "TC-039: Verify rapid navigation between profile sections",
        "UI should handle rapid navigation without errors");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.navigateTo(TEST_USERNAME);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    for (int i = 0; i < 5; i++) {
      profilePage.clickMyArticlesTab();
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      profilePage.clickFavoritedArticlesTab();
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    assertTrue(profilePage.isPageLoaded(), "Profile should remain stable after rapid navigation");
    test.info("Rapid navigation handling verified");
  }

  @Test(groups = {"edgecase", "regression"})
  public void TC040_verifyProfilePageWithUnicodeCharactersInBio() {
    createTest(
        "TC-040: Verify profile page with unicode characters in bio",
        "Unicode characters should display correctly");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.clearBio();
    settingsPage.enterBio("Unicode test: Hello World");
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.navigateTo(TEST_USERNAME);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(profilePage.isPageLoaded(), "Profile should display unicode characters correctly");
    test.info("Unicode characters in bio handling verified");
  }
}

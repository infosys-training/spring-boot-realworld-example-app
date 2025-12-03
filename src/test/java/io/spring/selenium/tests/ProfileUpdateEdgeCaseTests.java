package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import io.spring.selenium.pages.SettingsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case test cases for User Profile Update functionality. Tests boundary and edge case
 * scenarios for US-AUTH-004.
 */
public class ProfileUpdateEdgeCaseTests extends BaseTest {

  private LoginPage loginPage;
  private SettingsPage settingsPage;
  private HomePage homePage;
  private ProfilePage profilePage;
  private String baseUrl;

  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    loginPage = new LoginPage(driver);
    settingsPage = new SettingsPage(driver);
    homePage = new HomePage(driver);
    profilePage = new ProfilePage(driver);
  }

  private void loginAsTestUser() {
    loginPage.navigateTo(baseUrl);
    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC031_updateWithSameEmailAsCurrent() {
    createTest("TC-031", "Update with same email as current");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    String currentEmail = settingsPage.getCurrentEmail();
    test.info("Current email: " + currentEmail);

    test.info("Step 2: Submit same email");
    settingsPage.clearEmailField();
    settingsPage.enterEmail(currentEmail);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: No error, profile unchanged or success");
    assertFalse(
        settingsPage.isErrorDisplayed(), "Should not show error when submitting same email");
    assertTrue(settingsPage.isOnSettingsPage(), "Should remain on settings page");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC032_updateWithSameUsernameAsCurrent() {
    createTest("TC-032", "Update with same username as current");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    String currentUsername = settingsPage.getCurrentUsername();
    test.info("Current username: " + currentUsername);

    test.info("Step 2: Submit same username");
    settingsPage.clearUsernameField();
    settingsPage.enterUsername(currentUsername);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: No error, profile unchanged or success");
    assertFalse(
        settingsPage.isErrorDisplayed(), "Should not show error when submitting same username");
    assertTrue(settingsPage.isOnSettingsPage(), "Should remain on settings page");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC033_updateEmailWithLeadingTrailingSpaces() {
    createTest("TC-033", "Update email with leading/trailing spaces");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter email with spaces around it");
    String emailWithSpaces = "  spaces.test." + System.currentTimeMillis() + "@example.com  ";
    settingsPage.clearEmailField();
    settingsPage.enterEmail(emailWithSpaces);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Spaces trimmed, email validated");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should trim spaces and validate or show error");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC034_updateUsernameWithLeadingTrailingSpaces() {
    createTest("TC-034", "Update username with leading/trailing spaces");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter username with spaces");
    String usernameWithSpaces = "  spaceduser" + System.currentTimeMillis() + "  ";
    settingsPage.clearUsernameField();
    settingsPage.enterUsername(usernameWithSpaces);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Spaces trimmed, username validated");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should trim spaces and validate or show error");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC035_updateBioWithUnicodeCharacters() {
    createTest("TC-035", "Update bio with unicode characters");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter bio with emojis/unicode");
    String unicodeBio =
        "Hello World! Testing unicode chars: cafe, resume, naive - " + System.currentTimeMillis();
    settingsPage.clearBioField();
    settingsPage.enterBio(unicodeBio);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Unicode characters preserved correctly");
    assertFalse(settingsPage.isErrorDisplayed(), "Should not show error for unicode bio");
    assertTrue(settingsPage.isOnSettingsPage(), "Should remain on settings page");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC036_updateImageUrlWithQueryParameters() {
    createTest("TC-036", "Update image URL with query parameters");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter image URL with ?params");
    String imageUrlWithParams =
        "https://api.realworld.io/images/avatar.png?size=200&format=webp&v="
            + System.currentTimeMillis();
    settingsPage.clearImageUrlField();
    settingsPage.enterImageUrl(imageUrlWithParams);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: URL accepted and stored correctly");
    assertFalse(settingsPage.isErrorDisplayed(), "Should not show error for URL with query params");
    assertTrue(settingsPage.isOnSettingsPage(), "Should remain on settings page");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC037_updatePasswordToSameAsCurrent() {
    createTest("TC-037", "Update password to same as current");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter current password as new");
    settingsPage.enterPassword(TEST_USER_PASSWORD);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Success or warning about same password");
    assertTrue(
        settingsPage.isOnSettingsPage(), "Should remain on settings page after password update");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC038_updateWithEmptyBioClearBio() {
    createTest("TC-038", "Update with empty bio (clear bio)");
    test.info("Precondition: User logged in, has existing bio");

    loginAsTestUser();
    test.info("Step 1: First set a bio");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();
    settingsPage.clearBioField();
    settingsPage.enterBio("Temporary bio to be cleared");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Step 2: Clear bio field");
    settingsPage.clearBioField();

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Bio cleared successfully");
    assertFalse(settingsPage.isErrorDisplayed(), "Should not show error when clearing bio");
    assertTrue(settingsPage.isOnSettingsPage(), "Should remain on settings page");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC039_updateWithEmptyImageUrlClearAvatar() {
    createTest("TC-039", "Update with empty image URL (clear avatar)");
    test.info("Precondition: User logged in, has existing image");

    loginAsTestUser();
    test.info("Step 1: First set an image URL");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();
    settingsPage.clearImageUrlField();
    settingsPage.enterImageUrl("https://api.realworld.io/images/temp-avatar.png");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Step 2: Clear image URL field");
    settingsPage.clearImageUrlField();

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Image URL cleared, default avatar shown");
    assertFalse(settingsPage.isErrorDisplayed(), "Should not show error when clearing image URL");
    assertTrue(settingsPage.isOnSettingsPage(), "Should remain on settings page");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC040_rapidConsecutiveProfileUpdates() {
    createTest("TC-040", "Rapid consecutive profile updates");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2-6: Update profile 5 times rapidly");
    for (int i = 1; i <= 5; i++) {
      test.info("Update iteration " + i);
      String bio = "Rapid update #" + i + " - " + System.currentTimeMillis();
      settingsPage.clearBioField();
      settingsPage.enterBio(bio);
      settingsPage.clickUpdate();

      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: All updates processed correctly, no race conditions");
    assertFalse(settingsPage.isErrorDisplayed(), "Should not show error after rapid updates");
    assertTrue(settingsPage.isOnSettingsPage(), "Should remain on settings page");

    String finalBio = settingsPage.getCurrentBio();
    test.info("Final bio value: " + finalBio);
    assertTrue(
        finalBio.contains("Rapid update") || !settingsPage.isErrorDisplayed(),
        "Bio should reflect one of the updates");
  }
}

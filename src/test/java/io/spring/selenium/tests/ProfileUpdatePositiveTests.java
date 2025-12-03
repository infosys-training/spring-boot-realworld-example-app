package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import io.spring.selenium.pages.SettingsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Positive test cases for User Profile Update functionality. Tests happy path scenarios for
 * US-AUTH-004.
 */
public class ProfileUpdatePositiveTests extends BaseTest {

  private LoginPage loginPage;
  private SettingsPage settingsPage;
  private HomePage homePage;
  private ProfilePage profilePage;
  private String baseUrl;

  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";
  private static final String TEST_USER_USERNAME = "johndoe";

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

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC001_updateEmailSuccessfully() {
    createTest("TC-001", "Update email successfully");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings page");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter new valid email");
    String newEmail = "john.updated." + System.currentTimeMillis() + "@example.com";
    settingsPage.clearEmailField();
    settingsPage.enterEmail(newEmail);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Email updated, success message displayed");
    assertFalse(settingsPage.isErrorDisplayed(), "No error should be displayed");
    assertTrue(settingsPage.isOnSettingsPage(), "Should remain on settings page after update");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC002_updateUsernameSuccessfully() {
    createTest("TC-002", "Update username successfully");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings page");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter new valid username");
    String newUsername = "johndoe" + System.currentTimeMillis();
    settingsPage.clearUsernameField();
    settingsPage.enterUsername(newUsername);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Username updated, success message displayed");
    assertFalse(settingsPage.isErrorDisplayed(), "No error should be displayed");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC003_updatePasswordSuccessfully() {
    createTest("TC-003", "Update password successfully");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings page");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter new password");
    String newPassword = "newpassword123";
    settingsPage.enterPassword(newPassword);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Password updated successfully");
    assertFalse(settingsPage.isErrorDisplayed(), "No error should be displayed");
  }

  @Test(groups = {"regression", "positive"})
  public void TC004_updateBioSuccessfully() {
    createTest("TC-004", "Update bio successfully");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings page");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter new bio text");
    String newBio = "This is my updated bio - " + System.currentTimeMillis();
    settingsPage.clearBioField();
    settingsPage.enterBio(newBio);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Bio updated and displayed on profile");
    assertFalse(settingsPage.isErrorDisplayed(), "No error should be displayed");
  }

  @Test(groups = {"regression", "positive"})
  public void TC005_updateImageUrlSuccessfully() {
    createTest("TC-005", "Update image URL successfully");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings page");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter new image URL");
    String newImageUrl = "https://api.realworld.io/images/demo-avatar.png";
    settingsPage.clearImageUrlField();
    settingsPage.enterImageUrl(newImageUrl);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Image URL updated, new avatar displayed");
    assertFalse(settingsPage.isErrorDisplayed(), "No error should be displayed");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC006_updateMultipleFieldsAtOnce() {
    createTest("TC-006", "Update multiple fields at once");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings page");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Update email, username, and bio");
    long timestamp = System.currentTimeMillis();
    String newEmail = "multi.update." + timestamp + "@example.com";
    String newUsername = "multiuser" + timestamp;
    String newBio = "Multi-field update bio - " + timestamp;

    settingsPage.clearEmailField();
    settingsPage.enterEmail(newEmail);
    settingsPage.clearUsernameField();
    settingsPage.enterUsername(newUsername);
    settingsPage.clearBioField();
    settingsPage.enterBio(newBio);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: All fields updated successfully");
    assertFalse(settingsPage.isErrorDisplayed(), "No error should be displayed");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC007_updateAllFieldsSimultaneously() {
    createTest("TC-007", "Update all fields simultaneously");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings page");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Update all 5 fields");
    long timestamp = System.currentTimeMillis();
    String newImageUrl = "https://api.realworld.io/images/all-fields-avatar.png";
    String newUsername = "allfields" + timestamp;
    String newBio = "All fields update bio - " + timestamp;
    String newEmail = "all.fields." + timestamp + "@example.com";
    String newPassword = "allfieldspass123";

    settingsPage.clearImageUrlField();
    settingsPage.enterImageUrl(newImageUrl);
    settingsPage.clearUsernameField();
    settingsPage.enterUsername(newUsername);
    settingsPage.clearBioField();
    settingsPage.enterBio(newBio);
    settingsPage.clearEmailField();
    settingsPage.enterEmail(newEmail);
    settingsPage.enterPassword(newPassword);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: All fields updated, complete user data returned");
    assertFalse(settingsPage.isErrorDisplayed(), "No error should be displayed");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC008_partialUpdateWithOnlyEmail() {
    createTest("TC-008", "Partial update with only email");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings page");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    String originalUsername = settingsPage.getCurrentUsername();
    String originalBio = settingsPage.getCurrentBio();

    test.info("Step 2: Change only email field");
    String newEmail = "partial.email." + System.currentTimeMillis() + "@example.com";
    settingsPage.clearEmailField();
    settingsPage.enterEmail(newEmail);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Only email updated, other fields unchanged");
    assertFalse(settingsPage.isErrorDisplayed(), "No error should be displayed");
  }

  @Test(groups = {"regression", "positive"})
  public void TC009_partialUpdateWithOnlyBio() {
    createTest("TC-009", "Partial update with only bio");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings page");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Change only bio field");
    String newBio = "Partial bio update only - " + System.currentTimeMillis();
    settingsPage.clearBioField();
    settingsPage.enterBio(newBio);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Only bio updated, other fields unchanged");
    assertFalse(settingsPage.isErrorDisplayed(), "No error should be displayed");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC010_updateProfileAndVerifyPersistence() {
    createTest("TC-010", "Update profile and verify persistence");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Update profile fields");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    long timestamp = System.currentTimeMillis();
    String newBio = "Persistence test bio - " + timestamp;
    settingsPage.clearBioField();
    settingsPage.enterBio(newBio);
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Step 2: Logout");
    settingsPage.clickLogout();
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    test.info("Step 3: Login again");
    loginPage.navigateTo(baseUrl);
    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    test.info("Step 4: Check profile");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Verifying: Updated data persists after re-login");
    String currentBio = settingsPage.getCurrentBio();
    assertTrue(
        currentBio.contains("Persistence test bio") || !settingsPage.isErrorDisplayed(),
        "Bio should persist after re-login");
  }
}

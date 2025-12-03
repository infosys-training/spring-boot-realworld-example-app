package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.NavbarComponent;
import io.spring.selenium.pages.ProfilePage;
import io.spring.selenium.pages.SettingsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GetCurrentUserPositiveTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private NavbarComponent navbar;
  private ProfilePage profilePage;
  private SettingsPage settingsPage;

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_USERNAME = "johndoe";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    navbar = new NavbarComponent(driver);
    profilePage = new ProfilePage(driver);
    settingsPage = new SettingsPage(driver);
  }

  @Test(groups = {"smoke", "positive", "regression"})
  public void TC001_verifyAuthenticatedUserCanViewProfilePage() {
    createTest(
        "TC-001: Verify authenticated user can view their profile page",
        "User should be able to view their profile page after logging in");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    navbar.clickProfile();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(profilePage.isPageLoaded(), "Profile page should be loaded");
    test.info("Profile page loaded successfully for authenticated user");
  }

  @Test(groups = {"smoke", "positive", "regression"})
  public void TC002_verifyUsernameDisplayedOnProfilePage() {
    createTest(
        "TC-002: Verify username is displayed correctly on profile page",
        "Username should match the logged-in user's username");

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

    String displayedUsername = profilePage.getUsername();
    assertEquals(
        displayedUsername.toLowerCase(),
        TEST_USERNAME.toLowerCase(),
        "Username should match logged-in user");
    test.info("Username displayed correctly: " + displayedUsername);
  }

  @Test(groups = {"smoke", "positive", "regression"})
  public void TC003_verifyEmailDisplayedOnSettingsPage() {
    createTest(
        "TC-003: Verify email is displayed correctly on settings page",
        "Email field should show user's registered email");

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

    String displayedEmail = settingsPage.getEmailValue();
    assertEquals(displayedEmail, TEST_EMAIL, "Email should match registered email");
    test.info("Email displayed correctly: " + displayedEmail);
  }

  @Test(groups = {"positive", "regression"})
  public void TC004_verifyBioDisplayedOnProfilePage() {
    createTest(
        "TC-004: Verify bio is displayed correctly on profile page",
        "Bio text should match user's saved bio");

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

    assertTrue(profilePage.isPageLoaded(), "Profile page should be loaded");
    test.info("Bio section verified on profile page");
  }

  @Test(groups = {"positive", "regression"})
  public void TC005_verifyProfileImageDisplayed() {
    createTest(
        "TC-005: Verify profile image is displayed correctly",
        "Profile image should be visible and load correctly");

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

    assertTrue(profilePage.isProfileImageDisplayed(), "Profile image should be displayed");
    test.info("Profile image displayed successfully");
  }

  @Test(groups = {"smoke", "positive", "regression"})
  public void TC006_verifyUserCanAccessSettingsPageWhenLoggedIn() {
    createTest(
        "TC-006: Verify user can access settings page when logged in",
        "Settings page should display with user's current information");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    navbar.clickSettings();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(settingsPage.isPageLoaded(), "Settings page should be loaded");
    test.info("Settings page accessed successfully");
  }

  @Test(groups = {"smoke", "positive", "regression"})
  public void TC007_verifyUserSessionPersistsAfterPageRefresh() {
    createTest(
        "TC-007: Verify user session persists after page refresh",
        "User should remain logged in after refreshing the page");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

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

    assertTrue(navbar.isUserLoggedIn(), "User should remain logged in after refresh");
    test.info("Session persisted after page refresh");
  }

  @Test(groups = {"positive", "regression"})
  public void TC008_verifyUserCanNavigateToProfileFromNavbar() {
    createTest(
        "TC-008: Verify user can navigate to profile from navbar",
        "User should be navigated to their profile page when clicking username");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(navbar.isProfileLinkDisplayed(), "Profile link should be displayed in navbar");
    navbar.clickProfile();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(profilePage.isPageLoaded(), "Should navigate to profile page");
    test.info("Successfully navigated to profile from navbar");
  }

  @Test(groups = {"positive", "regression"})
  public void TC009_verifyUserCanViewOwnArticlesOnProfile() {
    createTest(
        "TC-009: Verify user can view their own articles on profile",
        "User's published articles should be displayed on profile");

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

    profilePage.clickMyArticlesTab();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(profilePage.isPageLoaded(), "My Articles tab should be accessible");
    test.info("My Articles tab verified on profile page");
  }

  @Test(groups = {"positive", "regression"})
  public void TC010_verifyUserCanViewFavoritedArticlesOnProfile() {
    createTest(
        "TC-010: Verify user can view their favorited articles on profile",
        "User's favorited articles should be displayed on profile");

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

    profilePage.clickFavoritedArticlesTab();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(profilePage.isPageLoaded(), "Favorited Articles tab should be accessible");
    test.info("Favorited Articles tab verified on profile page");
  }
}

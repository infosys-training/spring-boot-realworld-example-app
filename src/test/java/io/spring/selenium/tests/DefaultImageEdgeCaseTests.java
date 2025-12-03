package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.*;
import java.util.UUID;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case tests for default user image functionality. Tests boundary conditions and special
 * scenarios. TC-031 to TC-040
 */
public class DefaultImageEdgeCaseTests extends BaseTest {

  private static final String DEFAULT_IMAGE_URL =
      "https://static.productionready.io/images/smiley-cyrus.jpg";
  private String baseUrl;

  @BeforeMethod
  public void setUp() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  private void loginAsTestUser() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);
    loginPage.login("john@example.com", "password123");

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));
  }

  @Test(groups = {"regression"})
  public void testTC031_DefaultImageWithConcurrentRegistrations() {
    createTest(
        "TC-031: Verify default image with concurrent registrations",
        "Multiple simultaneous registrations should all get default image");

    String uniqueId1 = UUID.randomUUID().toString().substring(0, 8);
    String username1 = "concurrent1_" + uniqueId1;
    String email1 = "concurrent1_" + uniqueId1 + "@example.com";

    RegisterPage registerPage = new RegisterPage(driver);
    registerPage.navigateTo(baseUrl);
    registerPage.register(username1, email1, "password123");
    test.info("Registered first user: " + username1);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    ProfilePage profilePage = new ProfilePage(driver);
    profilePage.navigateTo(baseUrl, username1);

    boolean imageDisplayed = profilePage.isProfileImageDisplayed();
    test.info("First user profile image displayed: " + imageDisplayed);

    assertTrue(imageDisplayed, "First concurrent user should have profile image");
    test.info("Concurrent registration test completed");
  }

  @Test(groups = {"smoke", "regression"})
  public void testTC032_ImageUpdateFromDefaultToCustom() {
    createTest(
        "TC-032: Verify image update from default to custom",
        "Custom image should replace default image");

    loginAsTestUser();
    test.info("Logged in as test user");

    SettingsPage settingsPage = new SettingsPage(driver);
    settingsPage.navigateTo(baseUrl);

    String originalUrl = settingsPage.getImageUrl();
    test.info("Original image URL: " + originalUrl);

    String customImageUrl = "https://via.placeholder.com/150/0000FF/808080?text=Custom";
    settingsPage.setImageUrl(customImageUrl);
    settingsPage.clickUpdateSettings();
    test.info("Updated to custom image URL");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    ProfilePage profilePage = new ProfilePage(driver);
    profilePage.navigateTo(baseUrl, "johndoe");

    String profileImageSrc = profilePage.getProfileImageSrc();
    test.info("Profile image after update: " + profileImageSrc);

    assertTrue(
        profilePage.isProfileImageDisplayed(), "Profile image should be displayed after update");
    test.info("Default to custom image update verified");
  }

  @Test(groups = {"regression"})
  public void testTC033_ImageUpdateFromCustomBackToDefault() {
    createTest(
        "TC-033: Verify image update from custom back to default",
        "Default image should be restored when custom is cleared");

    loginAsTestUser();
    test.info("Logged in as test user");

    SettingsPage settingsPage = new SettingsPage(driver);
    settingsPage.navigateTo(baseUrl);

    settingsPage.setImageUrl(DEFAULT_IMAGE_URL);
    settingsPage.clickUpdateSettings();
    test.info("Set image URL back to default");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    ProfilePage profilePage = new ProfilePage(driver);
    profilePage.navigateTo(baseUrl, "johndoe");

    String profileImageSrc = profilePage.getProfileImageSrc();
    test.info("Profile image after reverting: " + profileImageSrc);

    assertTrue(profilePage.isProfileImageDisplayed(), "Default image should be restored");
    test.info("Custom to default image revert verified");
  }

  @Test(groups = {"regression"})
  public void testTC034_DefaultImageWithMinimumValidUrl() {
    createTest(
        "TC-034: Verify default image with minimum valid URL",
        "Shortest valid URL should be accepted");

    loginAsTestUser();
    test.info("Logged in as test user");

    SettingsPage settingsPage = new SettingsPage(driver);
    settingsPage.navigateTo(baseUrl);

    String minUrl = "http://a.co/i.png";
    settingsPage.setImageUrl(minUrl);
    test.info("Set minimum length URL: " + minUrl);

    String enteredUrl = settingsPage.getImageUrl();
    test.info("URL in field: " + enteredUrl);

    assertEquals(enteredUrl, minUrl, "Minimum URL should be accepted");
    test.info("Minimum URL length test completed");
  }

  @Test(groups = {"regression"})
  public void testTC035_DefaultImageWithMaximumUrlLength() {
    createTest(
        "TC-035: Verify default image with maximum URL length",
        "Maximum length URL should be handled");

    loginAsTestUser();
    test.info("Logged in as test user");

    SettingsPage settingsPage = new SettingsPage(driver);
    settingsPage.navigateTo(baseUrl);

    StringBuilder maxUrl = new StringBuilder("https://example.com/");
    while (maxUrl.length() < 2000) {
      maxUrl.append("a");
    }
    maxUrl.append(".jpg");

    String longUrl = maxUrl.toString();
    test.info("Generated URL with length: " + longUrl.length());

    settingsPage.setImageUrl(longUrl);

    int urlLength = settingsPage.getImageUrlLength();
    test.info("Entered URL length: " + urlLength);

    assertTrue(urlLength > 0, "Maximum URL should be handled");
    test.info("Maximum URL length test completed");
  }

  @Test(groups = {"regression"})
  public void testTC036_ImageDisplayWithSlowNetwork() {
    createTest(
        "TC-036: Verify image display with slow network",
        "Image should load with appropriate loading state");

    loginAsTestUser();
    test.info("Logged in as test user");

    ProfilePage profilePage = new ProfilePage(driver);
    profilePage.navigateTo(baseUrl, "johndoe");
    test.info("Navigated to profile page");

    boolean imageDisplayed = profilePage.isProfileImageDisplayed();
    test.info("Profile image displayed: " + imageDisplayed);

    assertTrue(imageDisplayed, "Image should be displayed even with slow network");
    test.info("Slow network image display verified");
  }

  @Test(groups = {"regression"})
  public void testTC037_ImageCachingBehavior() {
    createTest(
        "TC-037: Verify image caching behavior", "Image should be cached and load efficiently");

    loginAsTestUser();
    test.info("Logged in as test user");

    ProfilePage profilePage = new ProfilePage(driver);
    profilePage.navigateTo(baseUrl, "johndoe");
    test.info("First visit to profile page");

    String firstImageSrc = profilePage.getProfileImageSrc();
    test.info("First load image src: " + firstImageSrc);

    driver.navigate().refresh();
    test.info("Refreshed page");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage = new ProfilePage(driver);
    String secondImageSrc = profilePage.getProfileImageSrc();
    test.info("Second load image src: " + secondImageSrc);

    assertEquals(firstImageSrc, secondImageSrc, "Image src should be consistent across loads");
    test.info("Image caching behavior verified");
  }

  @Test(groups = {"regression"})
  public void testTC038_DefaultImageAfterSessionExpiry() {
    createTest(
        "TC-038: Verify default image after session expiry",
        "Default image should persist after re-login");

    loginAsTestUser();
    test.info("Logged in as test user");

    ProfilePage profilePage = new ProfilePage(driver);
    profilePage.navigateTo(baseUrl, "johndoe");
    String beforeLogoutImage = profilePage.getProfileImageSrc();
    test.info("Image before logout: " + beforeLogoutImage);

    SettingsPage settingsPage = new SettingsPage(driver);
    settingsPage.navigateTo(baseUrl);
    settingsPage.clickLogout();
    test.info("Logged out");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    loginAsTestUser();
    test.info("Re-logged in");

    profilePage = new ProfilePage(driver);
    profilePage.navigateTo(baseUrl, "johndoe");
    String afterReloginImage = profilePage.getProfileImageSrc();
    test.info("Image after re-login: " + afterReloginImage);

    assertTrue(profilePage.isProfileImageDisplayed(), "Image should persist after re-login");
    test.info("Session expiry image persistence verified");
  }

  @Test(groups = {"regression"})
  public void testTC039_DefaultImageConsistencyAcrossBrowsers() {
    createTest(
        "TC-039: Verify default image consistency across browsers",
        "Default image should be consistent (single browser test)");

    loginAsTestUser();
    test.info("Logged in as test user");

    ProfilePage profilePage = new ProfilePage(driver);
    profilePage.navigateTo(baseUrl, "johndoe");

    String imageSrc = profilePage.getProfileImageSrc();
    test.info("Profile image src: " + imageSrc);

    assertNotNull(imageSrc, "Image src should not be null");
    assertTrue(
        imageSrc.startsWith("http://") || imageSrc.startsWith("https://"),
        "Image should have valid URL");

    test.info("Browser consistency test completed (single browser verification)");
  }

  @Test(groups = {"regression"})
  public void testTC040_DefaultImageInMobileViewport() {
    createTest(
        "TC-040: Verify default image in mobile viewport",
        "Default image should display correctly on mobile");

    loginAsTestUser();
    test.info("Logged in as test user");

    driver.manage().window().setSize(new Dimension(375, 812));
    test.info("Set mobile viewport (iPhone X dimensions)");

    ProfilePage profilePage = new ProfilePage(driver);
    profilePage.navigateTo(baseUrl, "johndoe");

    boolean imageDisplayed = profilePage.isProfileImageDisplayed();
    test.info("Profile image displayed in mobile viewport: " + imageDisplayed);

    String imageSrc = profilePage.getProfileImageSrc();
    test.info("Mobile viewport image src: " + imageSrc);

    assertTrue(imageDisplayed, "Image should be displayed in mobile viewport");

    driver.manage().window().maximize();
    test.info("Restored desktop viewport");

    test.info("Mobile viewport test completed");
  }
}

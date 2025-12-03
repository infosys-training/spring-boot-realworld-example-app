package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProfileValidationTests extends BaseTest {

  private ProfilePage profilePage;
  private LoginPage loginPage;
  private HomePage homePage;

  private static final String TEST_USER_JOHNDOE = "johndoe";
  private static final String TEST_EMAIL_JOHNDOE = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final long PAGE_LOAD_TIMEOUT_SECONDS = 5;

  @BeforeMethod
  public void setupPages() {
    profilePage = new ProfilePage(driver);
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
  }

  @Test(
      groups = {"regression"},
      description = "TC-011: Profile URL format validation")
  public void testTC011_ProfileUrlFormatValidation() {
    createTest(
        "TC-011: Profile URL format validation", "Verify profile URL follows correct format");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    String currentUrl = profilePage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/profile/" + TEST_USER_JOHNDOE),
        "URL should contain /profile/{username}");

    test.info("Profile URL format validated: " + currentUrl);
  }

  @Test(
      groups = {"regression"},
      description = "TC-012: Profile page loads within acceptable time")
  public void testTC012_ProfilePageLoadsWithinAcceptableTime() {
    createTest(
        "TC-012: Profile page loads within acceptable time", "Verify page loads within 5 seconds");

    long startTime = System.currentTimeMillis();
    profilePage.navigateToProfile(TEST_USER_JOHNDOE);
    long endTime = System.currentTimeMillis();

    long loadTime = endTime - startTime;
    assertTrue(
        loadTime < PAGE_LOAD_TIMEOUT_SECONDS * 1000,
        "Page should load within " + PAGE_LOAD_TIMEOUT_SECONDS + " seconds");

    test.info("Page loaded in " + loadTime + "ms");
  }

  @Test(
      groups = {"regression"},
      description = "TC-013: Profile image has valid src attribute")
  public void testTC013_ProfileImageHasValidSrcAttribute() {
    createTest(
        "TC-013: Profile image has valid src attribute", "Verify image element has valid src");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfileImageDisplayed(), "Profile image should be displayed");
    String imageSrc = profilePage.getProfileImageSrc();
    assertNotNull(imageSrc, "Image src should not be null");
    assertTrue(imageSrc.length() > 0, "Image src should not be empty");

    test.info("Profile image src: " + imageSrc);
  }

  @Test(
      groups = {"regression"},
      description = "TC-014: Username displayed matches URL parameter")
  public void testTC014_UsernameDisplayedMatchesUrlParameter() {
    createTest(
        "TC-014: Username displayed matches URL parameter",
        "Verify displayed username matches URL");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    String displayedUsername = profilePage.getUsername();
    String currentUrl = profilePage.getCurrentUrl();

    assertTrue(currentUrl.contains(displayedUsername), "URL should contain the displayed username");
    assertEquals(
        displayedUsername, TEST_USER_JOHNDOE, "Displayed username should match URL parameter");

    test.info("Username validation passed: " + displayedUsername);
  }

  @Test(
      groups = {"regression"},
      description = "TC-015: Bio text is properly rendered")
  public void testTC015_BioTextIsProperlyRendered() {
    createTest(
        "TC-015: Bio text is properly rendered", "Verify bio text renders without HTML injection");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    String bio = profilePage.getBio();
    if (bio != null && !bio.isEmpty()) {
      assertFalse(bio.contains("<script>"), "Bio should not contain script tags");
      assertFalse(bio.contains("<iframe>"), "Bio should not contain iframe tags");
    }

    test.info("Bio text properly rendered: " + bio);
  }

  @Test(
      groups = {"regression"},
      description = "TC-016: Profile page has correct page title")
  public void testTC016_ProfilePageHasCorrectPageTitle() {
    createTest("TC-016: Profile page has correct page title", "Verify page title is set correctly");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    String pageTitle = profilePage.getPageTitle();
    assertNotNull(pageTitle, "Page title should not be null");
    assertTrue(pageTitle.length() > 0, "Page title should not be empty");

    test.info("Page title: " + pageTitle);
  }

  @Test(
      groups = {"regression"},
      description = "TC-017: Profile page structure is correct")
  public void testTC017_ProfilePageStructureIsCorrect() {
    createTest(
        "TC-017: Profile page structure is correct", "Verify page contains required sections");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isUserInfoSectionDisplayed(), "User info section should be present");
    assertTrue(profilePage.isArticlesToggleDisplayed(), "Articles toggle should be present");

    test.info("Profile page structure validated");
  }

  @Test(
      groups = {"regression"},
      description = "TC-018: Follow button not shown for own profile")
  public void testTC018_FollowButtonNotShownForOwnProfile() {
    createTest(
        "TC-018: Follow button not shown for own profile",
        "Verify no follow button on own profile");

    loginPage.login(TEST_EMAIL_JOHNDOE, TEST_PASSWORD);
    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertFalse(
        profilePage.isFollowButtonDisplayed(),
        "Follow button should not be displayed on own profile");

    test.info("Follow button correctly hidden on own profile");
  }

  @Test(
      groups = {"regression"},
      description = "TC-019: My Articles tab is displayed")
  public void testTC019_MyArticlesTabIsDisplayed() {
    createTest("TC-019: My Articles tab is displayed", "Verify My Articles tab is visible");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isMyArticlesTabDisplayed(), "My Articles tab should be displayed");

    test.info("My Articles tab is displayed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-020: Favorited Articles tab is displayed")
  public void testTC020_FavoritedArticlesTabIsDisplayed() {
    createTest(
        "TC-020: Favorited Articles tab is displayed", "Verify Favorited Articles tab is visible");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(
        profilePage.isFavoritedArticlesTabDisplayed(),
        "Favorited Articles tab should be displayed");

    test.info("Favorited Articles tab is displayed");
  }
}

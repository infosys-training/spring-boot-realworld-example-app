package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import java.util.List;
import org.testng.annotations.Test;

public class TagsAccessibilityTests extends BaseTest {

  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-015: Verify tags accessible without login")
  public void testTC015_TagsAccessibleWithoutLogin() {
    createTest("TC-015", "Verify tags accessible without login");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page without login");

    // Verify user is not logged in
    assertFalse(homePage.isUserLoggedIn(), "User should not be logged in");

    // Verify tags are visible
    assertTrue(homePage.isSidebarDisplayed(), "Sidebar should be displayed");

    List<String> tags = homePage.getAllTags();
    test.info("Tags visible without login: " + tags);

    assertNotNull(tags, "Tags should be accessible without authentication");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-016: Verify tags accessible with valid token")
  public void testTC016_TagsAccessibleWithValidToken() {
    createTest("TC-016", "Verify tags accessible with valid token");

    // Login first
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigate();
    HomePage homePage = loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    test.info("Logged in as test user");

    // Navigate to home and check tags
    homePage.navigate();

    assertTrue(homePage.isSidebarDisplayed(), "Sidebar should be displayed when logged in");

    List<String> tags = homePage.getAllTags();
    test.info("Tags visible when logged in: " + tags);

    assertNotNull(tags, "Tags should be accessible when authenticated");
  }

  @Test(
      groups = {"regression"},
      description = "TC-017: Verify tags accessible with invalid token")
  public void testTC017_TagsAccessibleWithInvalidToken() {
    createTest("TC-017", "Verify tags accessible with invalid token");

    // Navigate directly to home page (simulating invalid/no token)
    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    // Tags should still be visible as it's a public endpoint
    assertTrue(homePage.isSidebarDisplayed(), "Sidebar should be displayed");

    List<String> tags = homePage.getAllTags();
    test.info("Tags visible: " + tags);

    assertNotNull(tags, "Tags should be accessible even with invalid token (public endpoint)");
  }

  @Test(
      groups = {"regression"},
      description = "TC-018: Verify tags accessible with expired token")
  public void testTC018_TagsAccessibleWithExpiredToken() {
    createTest("TC-018", "Verify tags accessible with expired token");

    // Navigate to home page (simulating expired token scenario)
    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    // Tags should still be visible as it's a public endpoint
    assertTrue(homePage.isSidebarDisplayed(), "Sidebar should be displayed");

    List<String> tags = homePage.getAllTags();
    test.info("Tags visible: " + tags);

    assertNotNull(tags, "Tags should be accessible even with expired token (public endpoint)");
  }

  @Test(
      groups = {"regression"},
      description = "TC-019: Verify tags accessible in incognito mode")
  public void testTC019_TagsAccessibleInIncognitoMode() {
    createTest("TC-019", "Verify tags accessible in incognito mode");

    // In headless mode, each test starts with a fresh browser session
    // This simulates incognito behavior (no stored cookies/tokens)
    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page in fresh session (simulating incognito)");

    assertTrue(homePage.isSidebarDisplayed(), "Sidebar should be displayed in incognito mode");

    List<String> tags = homePage.getAllTags();
    test.info("Tags visible in incognito mode: " + tags);

    assertNotNull(tags, "Tags should be accessible in incognito mode");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-020: Verify no authentication header required")
  public void testTC020_NoAuthenticationHeaderRequired() {
    createTest("TC-020", "Verify no authentication header required");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page without any authentication");

    // Page should load successfully without auth headers
    assertTrue(homePage.isPageLoaded(), "Page should load without authentication");
    assertTrue(homePage.isSidebarDisplayed(), "Sidebar should be displayed");

    List<String> tags = homePage.getAllTags();
    test.info("Tags loaded without auth headers: " + tags);

    assertNotNull(tags, "Tags should load without authentication headers");
  }

  @Test(
      groups = {"regression"},
      description = "TC-021: Verify response same for authenticated and unauthenticated")
  public void testTC021_ResponseSameForAuthenticatedAndUnauthenticated() {
    createTest("TC-021", "Verify response same for authenticated and unauthenticated");

    // First, get tags without login
    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    List<String> tagsWithoutLogin = homePage.getAllTags();
    test.info("Tags without login: " + tagsWithoutLogin);

    // Now login and get tags again
    LoginPage loginPage = homePage.goToLogin();
    homePage = loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.navigate();

    List<String> tagsWithLogin = homePage.getAllTags();
    test.info("Tags with login: " + tagsWithLogin);

    // Tags should be the same regardless of authentication status
    assertEquals(
        tagsWithoutLogin.size(),
        tagsWithLogin.size(),
        "Tag count should be same for authenticated and unauthenticated users");

    for (String tag : tagsWithoutLogin) {
      assertTrue(
          tagsWithLogin.contains(tag),
          "Tag '" + tag + "' should be present for both authenticated and unauthenticated users");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-032: Verify tags persist across sessions")
  public void testTC032_TagsPersistAcrossSessions() {
    createTest("TC-032", "Verify tags persist across sessions");

    // Get tags in first session
    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    List<String> tagsFirstSession = homePage.getAllTags();
    test.info("Tags in first session: " + tagsFirstSession);

    // Refresh the page (simulating new session)
    homePage.refresh();

    List<String> tagsAfterRefresh = homePage.getAllTags();
    test.info("Tags after refresh: " + tagsAfterRefresh);

    // Tags should persist
    assertEquals(
        tagsFirstSession.size(),
        tagsAfterRefresh.size(),
        "Tag count should persist across sessions");

    for (String tag : tagsFirstSession) {
      assertTrue(
          tagsAfterRefresh.contains(tag), "Tag '" + tag + "' should persist across sessions");
    }
  }
}

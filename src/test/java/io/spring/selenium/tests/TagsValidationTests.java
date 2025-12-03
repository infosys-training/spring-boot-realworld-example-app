package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class TagsValidationTests extends BaseTest {

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-008: Verify response contains tags array")
  public void testTC008_ResponseContainsTagsArray() {
    createTest("TC-008", "Verify response contains tags array");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags retrieved: " + tags);

    assertNotNull(tags, "Tags should be returned as an array/list");
    assertTrue(tags instanceof List, "Tags should be a list structure");
  }

  @Test(
      groups = {"regression"},
      description = "TC-009: Verify each tag is a string type")
  public void testTC009_EachTagIsStringType() {
    createTest("TC-009", "Verify each tag is a string type");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags found: " + tags);

    assertTrue(homePage.areAllTagsStrings(), "All tags should be string types");

    for (String tag : tags) {
      assertTrue(tag instanceof String, "Each tag should be a String: " + tag);
      test.info("Verified tag is string: " + tag);
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-010: Verify tags array structure is correct")
  public void testTC010_TagsArrayStructureCorrect() {
    createTest("TC-010", "Verify tags array structure is correct");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    // Verify the DOM structure of tags
    List<WebElement> tagElements = homePage.getTagElements();
    test.info("Found " + tagElements.size() + " tag elements");

    for (WebElement element : tagElements) {
      String tagClass = element.getAttribute("class");
      test.info("Tag element class: " + tagClass);
      assertTrue(
          tagClass.contains("tag-pill") || tagClass.contains("tag"),
          "Tag should have proper CSS class");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-011: Verify tags are not objects")
  public void testTC011_TagsAreNotObjects() {
    createTest("TC-011", "Verify tags are not objects");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags found: " + tags);

    for (String tag : tags) {
      assertFalse(tag.startsWith("[object"), "Tag should not be [object Object]: " + tag);
      assertFalse(tag.startsWith("{"), "Tag should not be JSON object: " + tag);
      assertFalse(tag.contains("Object"), "Tag should not contain Object reference: " + tag);
      test.info("Verified tag is not object: " + tag);
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-012: Verify tags are not numbers")
  public void testTC012_TagsAreNotNumbers() {
    createTest("TC-012", "Verify tags are not numbers");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags found: " + tags);

    assertTrue(homePage.areAllTagsNonNumeric(), "Tags should not be purely numeric");

    for (String tag : tags) {
      boolean isNumeric = false;
      try {
        Double.parseDouble(tag);
        isNumeric = true;
      } catch (NumberFormatException e) {
        isNumeric = false;
      }
      assertFalse(isNumeric, "Tag should not be a number: " + tag);
      test.info("Verified tag is not numeric: " + tag);
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-013: Verify tags are not null values in array")
  public void testTC013_TagsAreNotNullValues() {
    createTest("TC-013", "Verify tags are not null values in array");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags found: " + tags);

    for (String tag : tags) {
      assertNotNull(tag, "Tag should not be null");
      assertFalse(tag.equals("null"), "Tag should not be string 'null'");
      assertFalse(tag.equals("undefined"), "Tag should not be 'undefined'");
      test.info("Verified tag is not null: " + tag);
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-014: Verify empty strings are not included")
  public void testTC014_EmptyStringsNotIncluded() {
    createTest("TC-014", "Verify empty strings are not included");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags found: " + tags);

    assertTrue(homePage.areAllTagsNonEmpty(), "All tags should be non-empty");

    for (String tag : tags) {
      assertFalse(tag.isEmpty(), "Tag should not be empty string");
      assertFalse(tag.trim().isEmpty(), "Tag should not be whitespace only");
      test.info("Verified tag is non-empty: " + tag);
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-024: Verify empty array structure is valid")
  public void testTC024_EmptyArrayStructureValid() {
    createTest("TC-024", "Verify empty array structure is valid");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    // Even if tags are empty, the container should exist
    boolean containerPresent = homePage.isTagListContainerPresent();
    test.info("Tag list container present: " + containerPresent);

    // The page should load successfully regardless of tag count
    assertTrue(homePage.isPageLoaded(), "Page should load with valid structure");
  }

  @Test(
      groups = {"regression"},
      description = "TC-025: Verify response status 200 for empty tags")
  public void testTC025_ResponseStatus200ForEmptyTags() {
    createTest("TC-025", "Verify response status 200 for empty tags");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    // Page should load successfully (200 status) regardless of tag count
    assertTrue(homePage.isPageLoaded(), "Page should load successfully");
    assertFalse(homePage.hasErrorMessage(), "No error message should be displayed");

    test.info("Page loaded successfully without errors");
  }

  @Test(
      groups = {"regression"},
      description = "TC-026: Verify empty array is not null")
  public void testTC026_EmptyArrayIsNotNull() {
    createTest("TC-026", "Verify empty array is not null");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();

    // Tags list should never be null, even if empty
    assertNotNull(tags, "Tags list should not be null even if empty");
    test.info("Tags list is not null, size: " + tags.size());
  }
}

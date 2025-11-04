package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.ArticleQueryService;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ArticlesApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class ArticlesApiTest extends TestWithCurrentUser {
  @Autowired private MockMvc mvc;

  @MockBean private ArticleQueryService articleQueryService;

  @MockBean private ArticleCommandService articleCommandService;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_create_article_success() throws Exception {
    String title = "How to train your dragon";
    String slug = "how-to-train-your-dragon";
    String description = "Ever wonder how?";
    String body = "You have to believe";
    List<String> tagList = asList("reactjs", "angularjs", "dragons");
    Map<String, Object> param = prepareParam(title, description, body, tagList);

    ArticleData articleData =
        new ArticleData(
            "123",
            slug,
            title,
            description,
            body,
            null,
            false,
            0,
            new DateTime(),
            new DateTime(),
            tagList,
            new ProfileData("userid", user.getUsername(), user.getBio(), user.getImage(), false));

    when(articleCommandService.createArticle(any(), any()))
        .thenReturn(new Article(title, description, body, tagList, user.getId(), null));

    when(articleQueryService.findBySlug(eq(Article.toSlug(title)), any()))
        .thenReturn(Optional.empty());

    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles")
        .then()
        .statusCode(200)
        .body("article.title", equalTo(title))
        .body("article.favorited", equalTo(false))
        .body("article.body", equalTo(body))
        .body("article.favoritesCount", equalTo(0))
        .body("article.author.username", equalTo(user.getUsername()))
        .body("article.author.id", equalTo(null));

    verify(articleCommandService).createArticle(any(), any());
  }

  @Test
  public void should_get_error_message_with_wrong_parameter() throws Exception {
    String title = "How to train your dragon";
    String description = "Ever wonder how?";
    String body = "";
    String[] tagList = {"reactjs", "angularjs", "dragons"};
    Map<String, Object> param = prepareParam(title, description, body, asList(tagList));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles")
        .prettyPeek()
        .then()
        .statusCode(422)
        .body("errors.body[0]", equalTo("can't be empty"));
  }

  @Test
  public void should_get_error_message_with_duplicated_title() {
    String title = "How to train your dragon";
    String slug = "how-to-train-your-dragon";
    String description = "Ever wonder how?";
    String body = "You have to believe";
    String[] tagList = {"reactjs", "angularjs", "dragons"};
    Map<String, Object> param = prepareParam(title, description, body, asList(tagList));

    ArticleData articleData =
        new ArticleData(
            "123",
            slug,
            title,
            description,
            body,
            null,
            false,
            0,
            new DateTime(),
            new DateTime(),
            asList(tagList),
            new ProfileData("userid", user.getUsername(), user.getBio(), user.getImage(), false));

    when(articleQueryService.findBySlug(eq(Article.toSlug(title)), any()))
        .thenReturn(Optional.of(articleData));

    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles")
        .prettyPeek()
        .then()
        .statusCode(422);
  }

  @Test
  public void should_generate_summary_with_more_than_15_words() throws Exception {
    String body =
        "This is a test article body with more than fifteen words to ensure proper summary"
            + " generation functionality works correctly";
    Map<String, Object> param =
        new HashMap<String, Object>() {
          {
            put(
                "article",
                new HashMap<String, Object>() {
                  {
                    put("title", "Test");
                    put("description", "Test");
                    put("body", body);
                    put("tagList", asList("test"));
                  }
                });
          }
        };

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles/generate-summary")
        .then()
        .statusCode(200)
        .body(
            "summary",
            equalTo(
                "This is a test article body with more than fifteen words to ensure proper"
                    + " summary"));
  }

  @Test
  public void should_generate_summary_with_exactly_15_words() throws Exception {
    String body =
        "One two three four five six seven eight nine ten eleven twelve thirteen fourteen fifteen";
    Map<String, Object> param =
        new HashMap<String, Object>() {
          {
            put(
                "article",
                new HashMap<String, Object>() {
                  {
                    put("title", "Test");
                    put("description", "Test");
                    put("body", body);
                    put("tagList", asList("test"));
                  }
                });
          }
        };

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles/generate-summary")
        .then()
        .statusCode(200)
        .body("summary", equalTo(body));
  }

  @Test
  public void should_generate_summary_with_less_than_15_words() throws Exception {
    String body = "Short article with only seven words here";
    Map<String, Object> param =
        new HashMap<String, Object>() {
          {
            put(
                "article",
                new HashMap<String, Object>() {
                  {
                    put("title", "Test");
                    put("description", "Test");
                    put("body", body);
                    put("tagList", asList("test"));
                  }
                });
          }
        };

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles/generate-summary")
        .then()
        .statusCode(200)
        .body("summary", equalTo(body));
  }

  @Test
  public void should_generate_empty_summary_with_empty_body() throws Exception {
    Map<String, Object> param =
        new HashMap<String, Object>() {
          {
            put(
                "article",
                new HashMap<String, Object>() {
                  {
                    put("title", "Test");
                    put("description", "Test");
                    put("body", "");
                    put("tagList", asList("test"));
                  }
                });
          }
        };

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles/generate-summary")
        .then()
        .statusCode(200)
        .body("summary", equalTo(""));
  }

  @Test
  public void should_generate_empty_summary_with_null_body() throws Exception {
    Map<String, Object> param =
        new HashMap<String, Object>() {
          {
            put(
                "article",
                new HashMap<String, Object>() {
                  {
                    put("title", "Test");
                    put("description", "Test");
                    put("body", null);
                    put("tagList", asList("test"));
                  }
                });
          }
        };

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles/generate-summary")
        .then()
        .statusCode(200)
        .body("summary", equalTo(""));
  }

  @Test
  public void should_create_article_with_summary() throws Exception {
    String title = "Article with summary";
    String slug = "article-with-summary";
    String description = "Testing summary field";
    String body = "This is the article body with enough words for testing";
    String summary = "This is the article body with enough words";
    List<String> tagList = asList("testing", "summary");

    Map<String, Object> param =
        new HashMap<String, Object>() {
          {
            put(
                "article",
                new HashMap<String, Object>() {
                  {
                    put("title", title);
                    put("description", description);
                    put("body", body);
                    put("summary", summary);
                    put("tagList", tagList);
                  }
                });
          }
        };

    ArticleData articleData =
        new ArticleData(
            "123",
            slug,
            title,
            description,
            body,
            summary,
            false,
            0,
            new DateTime(),
            new DateTime(),
            tagList,
            new ProfileData("userid", user.getUsername(), user.getBio(), user.getImage(), false));

    when(articleCommandService.createArticle(any(), any()))
        .thenReturn(new Article(title, description, body, tagList, user.getId(), summary));

    when(articleQueryService.findBySlug(eq(Article.toSlug(title)), any()))
        .thenReturn(Optional.empty());

    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles")
        .then()
        .statusCode(200)
        .body("article.title", equalTo(title))
        .body("article.body", equalTo(body))
        .body("article.summary", equalTo(summary));

    verify(articleCommandService).createArticle(any(), any());
  }

  private HashMap<String, Object> prepareParam(
      final String title, final String description, final String body, final List<String> tagList) {
    return new HashMap<String, Object>() {
      {
        put(
            "article",
            new HashMap<String, Object>() {
              {
                put("title", title);
                put("description", description);
                put("body", body);
                put("tagList", tagList);
              }
            });
      }
    };
  }
}

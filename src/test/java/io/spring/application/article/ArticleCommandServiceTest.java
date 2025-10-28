package io.spring.application.article;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisArticleRepository;
import io.spring.infrastructure.repository.MyBatisUserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({
  ArticleCommandService.class,
  MyBatisArticleRepository.class,
  MyBatisUserRepository.class
})
public class ArticleCommandServiceTest extends DbTestBase {

  @Autowired private ArticleCommandService articleCommandService;

  @Autowired private ArticleRepository articleRepository;

  @Autowired private UserRepository userRepository;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User("test@example.com", "testuser", "password123", "Test Bio", "https://example.com/avatar.jpg");
    userRepository.save(user);
  }

  @Test
  public void should_create_article_with_valid_params() {
    String title = "How to train your dragon";
    String description = "Ever wonder how?";
    String body = "You have to believe";
    List<String> tagList = Arrays.asList("reactjs", "angularjs", "dragons");
    
    NewArticleParam param = NewArticleParam.builder()
        .title(title)
        .description(description)
        .body(body)
        .tagList(tagList)
        .build();

    Article article = articleCommandService.createArticle(param, user);

    Assertions.assertNotNull(article);
    Assertions.assertNotNull(article.getId());
    Assertions.assertEquals(title, article.getTitle());
    Assertions.assertEquals(description, article.getDescription());
    Assertions.assertEquals(body, article.getBody());
    Assertions.assertEquals(user.getId(), article.getUserId());
    Assertions.assertEquals("how-to-train-your-dragon", article.getSlug());
    Assertions.assertNotNull(article.getCreatedAt());
    Assertions.assertNotNull(article.getUpdatedAt());
    
    Optional<Article> saved = articleRepository.findById(article.getId());
    Assertions.assertTrue(saved.isPresent());
    Assertions.assertEquals(article.getId(), saved.get().getId());
  }

  @Test
  public void should_create_article_with_tags() {
    String title = "Spring Boot Best Practices";
    List<String> tagList = Arrays.asList("java", "spring", "backend");
    
    NewArticleParam param = NewArticleParam.builder()
        .title(title)
        .description("Best practices for Spring Boot")
        .body("Here are some best practices...")
        .tagList(tagList)
        .build();

    Article article = articleCommandService.createArticle(param, user);

    Assertions.assertNotNull(article.getTags());
    Assertions.assertEquals(3, article.getTags().size());
  }

  @Test
  public void should_create_article_with_empty_tag_list() {
    NewArticleParam param = NewArticleParam.builder()
        .title("Article without tags")
        .description("This article has no tags")
        .body("Some content here")
        .tagList(Collections.emptyList())
        .build();

    Article article = articleCommandService.createArticle(param, user);

    Assertions.assertNotNull(article);
    Assertions.assertNotNull(article.getTags());
    Assertions.assertEquals(0, article.getTags().size());
  }


  @Test
  public void should_generate_slug_from_title() {
    NewArticleParam param = NewArticleParam.builder()
        .title("This is a Test Title")
        .description("Description")
        .body("Body")
        .tagList(Collections.emptyList())
        .build();

    Article article = articleCommandService.createArticle(param, user);

    Assertions.assertEquals("this-is-a-test-title", article.getSlug());
  }

  @Test
  public void should_update_article_title() {
    Article article = new Article(
        "Original Title",
        "Original Description",
        "Original Body",
        Arrays.asList("tag1"),
        user.getId()
    );
    articleRepository.save(article);
    
    String originalSlug = article.getSlug();
    DateTime originalUpdatedAt = article.getUpdatedAt();
    
    UpdateArticleParam updateParam = new UpdateArticleParam("New Title", "", "");

    Article updated = articleCommandService.updateArticle(article, updateParam);

    Assertions.assertEquals("New Title", updated.getTitle());
    Assertions.assertEquals("new-title", updated.getSlug());
    Assertions.assertNotEquals(originalSlug, updated.getSlug());
    Assertions.assertEquals("Original Description", updated.getDescription());
    Assertions.assertEquals("Original Body", updated.getBody());
  }

  @Test
  public void should_update_article_description() {
    Article article = new Article(
        "Title",
        "Original Description",
        "Original Body",
        Arrays.asList("tag1"),
        user.getId()
    );
    articleRepository.save(article);

    UpdateArticleParam updateParam = new UpdateArticleParam("", "", "Updated Description");

    Article updated = articleCommandService.updateArticle(article, updateParam);

    Assertions.assertEquals("Title", updated.getTitle());
    Assertions.assertEquals("Updated Description", updated.getDescription());
    Assertions.assertEquals("Original Body", updated.getBody());
  }

  @Test
  public void should_update_article_body() {
    Article article = new Article(
        "Title",
        "Description",
        "Original Body",
        Arrays.asList("tag1"),
        user.getId()
    );
    articleRepository.save(article);

    UpdateArticleParam updateParam = new UpdateArticleParam("", "Updated Body", "");

    Article updated = articleCommandService.updateArticle(article, updateParam);

    Assertions.assertEquals("Title", updated.getTitle());
    Assertions.assertEquals("Description", updated.getDescription());
    Assertions.assertEquals("Updated Body", updated.getBody());
  }

  @Test
  public void should_update_multiple_fields() {
    Article article = new Article(
        "Original Title",
        "Original Description",
        "Original Body",
        Arrays.asList("tag1"),
        user.getId()
    );
    articleRepository.save(article);

    UpdateArticleParam updateParam = new UpdateArticleParam(
        "New Title",
        "New Body",
        "New Description"
    );

    Article updated = articleCommandService.updateArticle(article, updateParam);

    Assertions.assertEquals("New Title", updated.getTitle());
    Assertions.assertEquals("New Description", updated.getDescription());
    Assertions.assertEquals("New Body", updated.getBody());
  }

  @Test
  public void should_not_update_fields_when_empty_string() {
    Article article = new Article(
        "Original Title",
        "Original Description",
        "Original Body",
        Arrays.asList("tag1"),
        user.getId()
    );
    articleRepository.save(article);

    UpdateArticleParam updateParam = new UpdateArticleParam("", "", "");

    Article updated = articleCommandService.updateArticle(article, updateParam);

    Assertions.assertEquals("Original Title", updated.getTitle());
    Assertions.assertEquals("Original Description", updated.getDescription());
    Assertions.assertEquals("Original Body", updated.getBody());
  }

  @Test
  public void should_persist_article_updates_to_repository() {
    Article article = new Article(
        "Original Title",
        "Original Description",
        "Original Body",
        Arrays.asList("tag1"),
        user.getId()
    );
    articleRepository.save(article);
    String articleId = article.getId();

    UpdateArticleParam updateParam = new UpdateArticleParam("Updated Title", "", "");
    articleCommandService.updateArticle(article, updateParam);

    Optional<Article> retrieved = articleRepository.findById(articleId);
    Assertions.assertTrue(retrieved.isPresent());
    Assertions.assertEquals("Updated Title", retrieved.get().getTitle());
  }
}

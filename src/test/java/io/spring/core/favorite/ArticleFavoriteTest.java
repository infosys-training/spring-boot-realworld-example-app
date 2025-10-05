package io.spring.core.favorite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArticleFavoriteTest {

  @Test
  public void should_create_article_favorite_with_article_and_user() {
    ArticleFavorite favorite = new ArticleFavorite("article-123", "user-456");

    Assertions.assertEquals("article-123", favorite.getArticleId());
    Assertions.assertEquals("user-456", favorite.getUserId());
  }

  @Test
  public void should_create_with_no_args_constructor() {
    ArticleFavorite favorite = new ArticleFavorite();

    Assertions.assertNull(favorite.getArticleId());
    Assertions.assertNull(favorite.getUserId());
  }

  @Test
  public void should_be_equal_when_both_ids_match() {
    ArticleFavorite favorite1 = new ArticleFavorite("article-123", "user-456");
    ArticleFavorite favorite2 = new ArticleFavorite("article-123", "user-456");

    Assertions.assertEquals(favorite1, favorite2);
  }

  @Test
  public void should_not_be_equal_when_article_ids_differ() {
    ArticleFavorite favorite1 = new ArticleFavorite("article-123", "user-456");
    ArticleFavorite favorite2 = new ArticleFavorite("article-999", "user-456");

    Assertions.assertNotEquals(favorite1, favorite2);
  }

  @Test
  public void should_not_be_equal_when_user_ids_differ() {
    ArticleFavorite favorite1 = new ArticleFavorite("article-123", "user-456");
    ArticleFavorite favorite2 = new ArticleFavorite("article-123", "user-999");

    Assertions.assertNotEquals(favorite1, favorite2);
  }

  @Test
  public void should_not_be_equal_when_both_ids_differ() {
    ArticleFavorite favorite1 = new ArticleFavorite("article-123", "user-456");
    ArticleFavorite favorite2 = new ArticleFavorite("article-999", "user-999");

    Assertions.assertNotEquals(favorite1, favorite2);
  }

  @Test
  public void should_have_same_hash_code_when_equal() {
    ArticleFavorite favorite1 = new ArticleFavorite("article-123", "user-456");
    ArticleFavorite favorite2 = new ArticleFavorite("article-123", "user-456");

    Assertions.assertEquals(favorite1.hashCode(), favorite2.hashCode());
  }

  @Test
  public void should_not_be_equal_to_null() {
    ArticleFavorite favorite = new ArticleFavorite("article-123", "user-456");

    Assertions.assertNotEquals(favorite, null);
  }

  @Test
  public void should_be_equal_to_itself() {
    ArticleFavorite favorite = new ArticleFavorite("article-123", "user-456");

    Assertions.assertEquals(favorite, favorite);
  }
}

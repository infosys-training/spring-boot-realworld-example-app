package io.spring.infrastructure.repository;

import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.infrastructure.mybatis.mapper.ArticleFavoriteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MyBatisArticleFavoriteRepository implements ArticleFavoriteRepository {
  private static final Logger log = LoggerFactory.getLogger(MyBatisArticleFavoriteRepository.class);
  private ArticleFavoriteMapper mapper;

  @Autowired
  public MyBatisArticleFavoriteRepository(ArticleFavoriteMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  @Transactional(timeout = 10)
  public void save(ArticleFavorite articleFavorite) {
    log.debug(
        "Saving article favorite: articleId={}, userId={}",
        articleFavorite.getArticleId(),
        articleFavorite.getUserId());

    try {
      ArticleFavorite existing =
          mapper.find(articleFavorite.getArticleId(), articleFavorite.getUserId());
      if (existing == null) {
        mapper.insert(articleFavorite);
        log.debug("Successfully inserted article favorite");
      } else {
        log.debug("Article favorite already exists, skipping insert");
      }
    } catch (DuplicateKeyException e) {
      log.debug(
          "Duplicate key caught during concurrent insert, treating as success: {}", e.getMessage());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<ArticleFavorite> find(String articleId, String userId) {
    return Optional.ofNullable(mapper.find(articleId, userId));
  }

  @Override
  @Transactional(timeout = 10)
  public void remove(ArticleFavorite favorite) {
    log.debug(
        "Removing article favorite: articleId={}, userId={}",
        favorite.getArticleId(),
        favorite.getUserId());
    mapper.delete(favorite);
  }
}

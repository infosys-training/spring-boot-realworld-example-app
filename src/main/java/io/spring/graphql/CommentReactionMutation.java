package io.spring.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;
import graphql.execution.DataFetcherResult;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CommentQueryService;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentReaction;
import io.spring.core.comment.CommentReactionRepository;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.User;
import io.spring.graphql.DgsConstants.MUTATION;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.CommentPayload;
import lombok.AllArgsConstructor;

@DgsComponent
@AllArgsConstructor
public class CommentReactionMutation {
  private CommentReactionRepository reactionRepository;
  private CommentRepository commentRepository;
  private ArticleRepository articleRepository;
  private CommentQueryService commentQueryService;

  @DgsData(parentType = MUTATION.TYPE_NAME, field = MUTATION.LikeComment)
  public DataFetcherResult<CommentPayload> likeComment(
      @InputArgument("slug") String slug, @InputArgument("commentId") String commentId) {
    User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);
    Article article =
        articleRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    Comment comment =
        commentRepository
            .findById(article.getId(), commentId)
            .orElseThrow(ResourceNotFoundException::new);
    CommentReaction reaction =
        new CommentReaction(comment.getId(), user.getId(), CommentReaction.ReactionType.LIKE);
    reactionRepository.save(reaction);
    return DataFetcherResult.<CommentPayload>newResult()
        .data(CommentPayload.newBuilder().build())
        .localContext(comment)
        .build();
  }

  @DgsData(parentType = MUTATION.TYPE_NAME, field = MUTATION.UnlikeComment)
  public DataFetcherResult<CommentPayload> unlikeComment(
      @InputArgument("slug") String slug, @InputArgument("commentId") String commentId) {
    User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);
    Article article =
        articleRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    Comment comment =
        commentRepository
            .findById(article.getId(), commentId)
            .orElseThrow(ResourceNotFoundException::new);
    reactionRepository
        .find(comment.getId(), user.getId())
        .ifPresent(
            reaction -> {
              if (reaction.getReactionType() == CommentReaction.ReactionType.LIKE) {
                reactionRepository.remove(reaction);
              }
            });
    return DataFetcherResult.<CommentPayload>newResult()
        .data(CommentPayload.newBuilder().build())
        .localContext(comment)
        .build();
  }

  @DgsData(parentType = MUTATION.TYPE_NAME, field = MUTATION.DislikeComment)
  public DataFetcherResult<CommentPayload> dislikeComment(
      @InputArgument("slug") String slug, @InputArgument("commentId") String commentId) {
    User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);
    Article article =
        articleRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    Comment comment =
        commentRepository
            .findById(article.getId(), commentId)
            .orElseThrow(ResourceNotFoundException::new);
    CommentReaction reaction =
        new CommentReaction(comment.getId(), user.getId(), CommentReaction.ReactionType.DISLIKE);
    reactionRepository.save(reaction);
    return DataFetcherResult.<CommentPayload>newResult()
        .data(CommentPayload.newBuilder().build())
        .localContext(comment)
        .build();
  }

  @DgsData(parentType = MUTATION.TYPE_NAME, field = MUTATION.UndislikeComment)
  public DataFetcherResult<CommentPayload> undislikeComment(
      @InputArgument("slug") String slug, @InputArgument("commentId") String commentId) {
    User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);
    Article article =
        articleRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    Comment comment =
        commentRepository
            .findById(article.getId(), commentId)
            .orElseThrow(ResourceNotFoundException::new);
    reactionRepository
        .find(comment.getId(), user.getId())
        .ifPresent(
            reaction -> {
              if (reaction.getReactionType() == CommentReaction.ReactionType.DISLIKE) {
                reactionRepository.remove(reaction);
              }
            });
    return DataFetcherResult.<CommentPayload>newResult()
        .data(CommentPayload.newBuilder().build())
        .localContext(comment)
        .build();
  }
}

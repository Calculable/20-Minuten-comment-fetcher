package ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten;

import ch.ost.newspapercommenttodatabasefetcher.entity.*;
import ch.ost.newspapercommenttodatabasefetcher.helper.DateHelper;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleMetadata;
import ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse.ApiResponseComment;
import ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse.ApiResponseCommentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
/**
 * Converts the 20min API-Response into Entity-Objects that are compatible with the database
 */
public class CommentConverter {

    private String newspaperName;

    private String newspaperLanguageCode;

    private DateHelper dateHelper;

    public CommentConverter(@Value("${settings.20min.name}") String newspaperName, @Value("${settings.20min.languageCode}") String newspaperLanguageCode, @Autowired DateHelper dateHelper) {
        this.newspaperName = newspaperName;
        this.newspaperLanguageCode = newspaperLanguageCode;
        this.dateHelper = dateHelper;
    }

    /**
     * Takes the response from the 20 Minuten API and converts it into a custom structure that is compatible with the database
     *
     * @param originalStructure the result from the 20 Minten API for an article
     * @param articleMetadata   additional metadata about the article
     * @return a newspaper entity that contains the article and linked information (comments, comment-authors) and can be stored in the database
     */
    public Newspaper convertApiResponseToDatabaseEntities(ApiResponseCommentList originalStructure, ArticleMetadata articleMetadata) {
        Newspaper newspaper = createNewspaper();
        Article article = createArticle(articleMetadata, newspaper);
        newspaper.setArticles(List.of(article));
        addCommentsToArticle(originalStructure, newspaper, article);
        return newspaper;
    }

    private void addCommentsToArticle(ApiResponseCommentList originalStructure, Newspaper newspaper, Article article) {
        List<Comment> commentsForArticle = new ArrayList<>();
        Set<CommentAuthor> commentAuthors = new HashSet<>();
        convertComments(originalStructure.getComments(), newspaper, article, commentsForArticle, commentAuthors);
        article.setComments(commentsForArticle);
        article.setCommentsEnabled(originalStructure.getCommentingEnabled());
        newspaper.setCommentAuthors(commentAuthors);
    }

    private void convertComments(List<ApiResponseComment> comments, Newspaper newspaper, Article article, List<Comment> commentsForArticle, Set<CommentAuthor> commentAuthors) {
        for (ApiResponseComment comment : comments) {
            convertCommentsIncludingSubcomments(newspaper, article, comment, null, commentsForArticle, commentAuthors);
        }
    }

    private Article createArticle(ArticleMetadata articleMetadata, Newspaper newspaper) {
        Article article = new Article();
        article.setId(articleMetadata.getArticleId());
        article.setNewspaper(newspaper);
        article.setHeadline(articleMetadata.getArticleHeadline());
        article.setTimeOfPublication(articleMetadata.getArticlePublicationDate());
        article.setNewspaper(newspaper);
        article.setFullLink(articleMetadata.getArticleFullLink());
        article.setShortLink(articleMetadata.getArticleShortLink());
        article.setDescription(articleMetadata.getArticleDescription());
        article.setTopics(articleMetadata.getTopics().stream().map(this::convertToTopicEntity).collect(Collectors.toSet()));
        return article;
    }

    private Topic convertToTopicEntity(String topic) {
        return new Topic(topic);
    }

    private Newspaper createNewspaper() {
        Newspaper newspaper = new Newspaper();
        newspaper.setName(newspaperName);
        newspaper.setLanguageCode(newspaperLanguageCode);
        return newspaper;
    }

    private Comment convertCommentsIncludingSubcomments(Newspaper newspaper, Article article, ApiResponseComment comment, Comment parentComment, List<Comment> existingComments, Set<CommentAuthor> commentAuthors) {
        CommentAuthor commentAuthor = convertCommentAuthor(newspaper, comment, commentAuthors);
        commentAuthors.add(commentAuthor);

        Comment newComment = convertComment(article, comment, parentComment, commentAuthor);
        commentAuthor.setComments(List.of(newComment));

        existingComments.add(newComment);

        Reaction newReaction = convertReaction(comment, newComment);

        newComment.setReactions(List.of(newReaction));
        addSubCommentsToComment(newspaper, article, comment, existingComments, commentAuthors, newComment);
        return newComment;
    }

    private void addSubCommentsToComment(Newspaper newspaper, Article article, ApiResponseComment comment, List<Comment> existingComments, Set<CommentAuthor> commentAuthors, Comment newComment) {
        newComment.setReplies(new ArrayList<>());
        if (comment.getReplies() != null) {
            for (ApiResponseComment reply : comment.getReplies()) {
                Comment subComment = convertCommentsIncludingSubcomments(newspaper, article, reply, newComment, existingComments, commentAuthors);
                newComment.getReplies().add(subComment);
            }
        }
    }

    private Reaction convertReaction(ApiResponseComment comment, Comment newComment) {
        Reaction newReaction = new Reaction();
        newReaction.setAmountOfPositiveReactions(sumOfIntegers(comment.getReactions().getAwesome(), comment.getReactions().getExact(), comment.getReactions().getSmart()));
        newReaction.setAmountOfNegativeReactions(sumOfIntegers(comment.getReactions().getBad(), comment.getReactions().getNonsense(), comment.getReactions().getUnnecessary()));
        newReaction.setComment(newComment);
        return newReaction;
    }

    private Comment convertComment(Article article, ApiResponseComment comment, Comment parentComment, CommentAuthor commentAuthor) {
        Comment newComment = new Comment();
        newComment.setId(comment.getId());
        newComment.setArticle(article);
        newComment.setCommentAuthor(commentAuthor);
        newComment.setContent(comment.getBody());
        newComment.setCreatedAt(dateHelper.convertISO8601DateToLocalDateTime(comment.getCreatedAt()));
        newComment.setParentComment(parentComment);
        return newComment;
    }

    private CommentAuthor convertCommentAuthor(Newspaper newspaper, ApiResponseComment comment, Set<CommentAuthor> commentAuthors) {
        CommentAuthor commentAuthor;
        Optional<CommentAuthor> existingAuthor = findCommentAuthor(comment, commentAuthors);

        if (existingAuthor.isPresent()) {
            commentAuthor = existingAuthor.get();
        } else {
            commentAuthor = new CommentAuthor();
            commentAuthor.setNewspaper(newspaper);
            commentAuthor.setUsername(comment.getAuthorNickname());
        }
        return commentAuthor;
    }

    private Optional<CommentAuthor> findCommentAuthor(ApiResponseComment comment, Set<CommentAuthor> commentAuthors) {
        return commentAuthors.stream().filter(author -> author.getUsername().equals(comment.getAuthorNickname())).findFirst();
    }

    private int sumOfIntegers(Integer... values) {
        return Arrays.stream(values)
                .map(value -> Optional.ofNullable(value).orElse(0))
                .reduce(0, Integer::sum);
    }

}

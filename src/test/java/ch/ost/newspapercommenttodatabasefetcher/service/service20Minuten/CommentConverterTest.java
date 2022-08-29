package ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten;

import ch.ost.newspapercommenttodatabasefetcher.entity.Comment;
import ch.ost.newspapercommenttodatabasefetcher.entity.Newspaper;
import ch.ost.newspapercommenttodatabasefetcher.entity.Reaction;
import ch.ost.newspapercommenttodatabasefetcher.helper.DateHelper;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleMetadata;
import ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse.ApiResponseComment;
import ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse.ApiResponseCommentAuthorAvatar;
import ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse.ApiResponseCommentList;
import ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse.ApiResponseCommentReaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentConverterTest {

    CommentConverter commentConverter = new CommentConverter("20Minuten", "de", new DateHelper());
    ApiResponseCommentList commentList;
    ApiResponseComment comment;
    ArticleMetadata articleMetadata;

    @BeforeEach
    public void initializeApiStructure() {
        ApiResponseCommentReaction comment1Reaction = new ApiResponseCommentReaction();
        comment1Reaction.setAwesome(1);
        comment1Reaction.setBad(1);
        comment1Reaction.setExact(1);
        comment1Reaction.setNonsense(1);
        comment1Reaction.setSmart(1);
        comment1Reaction.setUnnecessary(1);

        ApiResponseCommentAuthorAvatar apiResponseCommentAuthorAvatar = new ApiResponseCommentAuthorAvatar();
        apiResponseCommentAuthorAvatar.setDark("exampleDark.png");
        apiResponseCommentAuthorAvatar.setLight("exampleLight.png");

        ApiResponseComment comment1Answer = new ApiResponseComment();
        comment1Answer.setCreatedAt("2022-03-07T10:35:32Z");
        comment1Answer.setId("comment1Answerid");
        comment1Answer.setBody("Comment 1 Answer Body");
        comment1Answer.setReplies(new ArrayList<>());
        comment1Answer.setReactions(comment1Reaction);
        comment1Answer.setAuthorNickname("demo author 2");
        comment1Answer.setAuthorAvatar(apiResponseCommentAuthorAvatar);

        comment = new ApiResponseComment();
        comment.setCreatedAt("2022-03-07T10:35:32Z");
        comment.setId("comment1id");
        comment.setBody("Comment 1 Body");
        comment.setReplies(List.of(comment1Answer));
        comment.setReactions(comment1Reaction);
        comment.setAuthorNickname("demo author");
        comment.setAuthorAvatar(apiResponseCommentAuthorAvatar);

        commentList = new ApiResponseCommentList();
        commentList.setCommentingEnabled(true);
        commentList.setTotalCount(10);
        commentList.setComments(List.of(comment));

        articleMetadata = new ArticleMetadata("articleId", "Headline", "https://www.linkToArticle.ch", "https://www.shortlinkToArticle.ch", "description", new HashSet<>(), LocalDateTime.now());
    }

    @Test
    public void apiResponseCanBeConvertedToDatabaseEntities() {
        Newspaper newspaper = commentConverter.convertApiResponseToDatabaseEntities(commentList, articleMetadata);
        assertThat(newspaper.getArticles()).hasSize(1);
        assertThat(newspaper.getCommentAuthors()).hasSize(2);
    }

    @Test
    public void answerCommentsAreRecursivelyConverted() {
        Newspaper newspaper = commentConverter.convertApiResponseToDatabaseEntities(commentList, articleMetadata);
        Comment aComment = newspaper.getArticles().stream().findFirst().get().getComments().stream().findFirst().get();

        if (aComment.getReplies().isEmpty()) {
            //this is the reply comment. Check if it has a parent comment
            assertThat(aComment.getParentComment().getReplies().stream().findFirst().get()).isEqualTo(aComment);
        } else {
            //this is the main comment. Check if it has a reply comment
            assertThat(aComment.getReplies().stream().findFirst().get().getParentComment()).isEqualTo(aComment);
        }
    }

    @Test
    public void duplicateAuthorsAreOnlyConvertedOnce() {
        ApiResponseComment anotherCommentWithTheSameAuthor = new ApiResponseComment();
        anotherCommentWithTheSameAuthor.setCreatedAt("2022-03-07T10:35:32Z");
        anotherCommentWithTheSameAuthor.setId("anotherCommentId");
        anotherCommentWithTheSameAuthor.setBody("Another Body");
        anotherCommentWithTheSameAuthor.setReplies(new ArrayList<>());
        anotherCommentWithTheSameAuthor.setReactions(new ApiResponseCommentReaction());
        anotherCommentWithTheSameAuthor.setAuthorNickname("demo author");
        Newspaper newspaper = commentConverter.convertApiResponseToDatabaseEntities(commentList, articleMetadata);
        assertThat(newspaper.getCommentAuthors()).hasSize(2);
    }

    @Test
    public void reactionsAreCorrectlySummarized() {
        ApiResponseCommentReaction newReaction = new ApiResponseCommentReaction();
        newReaction.setUnnecessary(1);
        newReaction.setSmart(3);
        newReaction.setExact(3);
        newReaction.setNonsense(2);
        newReaction.setBad(0);
        newReaction.setAwesome(1);

        comment.setReactions(newReaction);

        Newspaper newspaper = commentConverter.convertApiResponseToDatabaseEntities(commentList, articleMetadata);
        Reaction reaction = newspaper.getArticles().get(0).getComments().get(0).getReactions().get(0);

        assertThat(reaction.getAmountOfPositiveReactions()).isEqualTo(7);
        assertThat(reaction.getAmountOfNegativeReactions()).isEqualTo(3);
    }

    @Test
    public void nullReactionsAreCorrectlySummarized() {
        ApiResponseCommentReaction newReaction = new ApiResponseCommentReaction();
        newReaction.setAwesome(1);

        comment.setReactions(newReaction);

        Newspaper newspaper = commentConverter.convertApiResponseToDatabaseEntities(commentList, articleMetadata);
        Reaction reaction = newspaper.getArticles().get(0).getComments().get(0).getReactions().get(0);

        assertThat(reaction.getAmountOfPositiveReactions()).isEqualTo(1);
        assertThat(reaction.getAmountOfNegativeReactions()).isEqualTo(0);
    }
}

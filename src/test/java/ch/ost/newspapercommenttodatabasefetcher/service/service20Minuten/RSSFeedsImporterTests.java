package ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten;

import ch.ost.newspapercommenttodatabasefetcher.entity.Article;
import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotConvertApiResultToObjects;
import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotFetchFromApiException;
import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotSendNotificationToMicrosoftTeams;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleImportResult;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleMetadata;
import ch.ost.newspapercommenttodatabasefetcher.model.RSSFeedResult;
import ch.ost.newspapercommenttodatabasefetcher.repository.ArticleRepository;
import ch.ost.newspapercommenttodatabasefetcher.service.ImportReportSender;
import lombok.SneakyThrows;
import org.assertj.core.internal.bytebuddy.build.ToStringPlugin;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RSSFeedsImporterTests {

    RSSFeedsImporter rssFeedsImporter;

    @Mock
    CommentImporter commentImporter;

    @Mock
    RSSFeedsConverter feedFetcher;

    @Mock
    ImportReportSender importReportSender;

    @Mock
    ArticleRepository articleRepository;

    @SneakyThrows
    @BeforeEach
    void init() {
        rssFeedsImporter = new RSSFeedsImporter(1000, 100, 48, commentImporter, feedFetcher, importReportSender);
        Mockito.lenient().when(feedFetcher.fetchNewArticlesFromFeeds()).thenReturn(createMockRssFeedResult());
        Mockito.lenient().when(commentImporter.importCommentsForArticle(any(), eq(1000))).thenReturn(10);

        Mockito.lenient().when(articleRepository.existsById(eq("1"))).thenReturn(false);
        Mockito.lenient().when(articleRepository.existsById(eq("2"))).thenReturn(false);
        Mockito.lenient().when(articleRepository.existsById(eq("3"))).thenReturn(false);
        Mockito.lenient().when(articleRepository.existsById(eq("4"))).thenReturn(false);
        Mockito.lenient().when(articleRepository.existsById(eq("5"))).thenReturn(true);
        Mockito.lenient().when(articleRepository.findById(eq("5"))).thenReturn(createMockArticle(true, "5"));
        Mockito.lenient().when(articleRepository.existsById(eq("6"))).thenReturn(true);
        Mockito.lenient().when(articleRepository.findById(eq("6"))).thenReturn(createMockArticle(false, "6"));


    }

    private Optional<Article> createMockArticle(boolean commentsEnabled, String id) {
        Article article = new Article();
        article.setId(id);
        article.setCommentsEnabled(commentsEnabled);
        article.setTimeOfPublication(LocalDateTime.now().minusDays(5));
        return Optional.of(article);
    }

    private RSSFeedResult createMockRssFeedResult() {
        ArrayList<ArticleMetadata> articles = new ArrayList<>();
        articles.add(createMockArticleMetadata(1, false));
        articles.add(createMockArticleMetadata(2, false));
        articles.add(createMockArticleMetadata(3, false));
        articles.add(createMockArticleMetadata(4, true));
        articles.add(createMockArticleMetadata(5, true));
        articles.add(createMockArticleMetadata(6, false));

        Set<String> feedsWithoutErrorStrings = Set.of("https://www.example-feed.ch/without-error");
        Set<String> feedsWithErrorStrings = Set.of("https://www.example-feed.ch/with-error");
        return new RSSFeedResult(articles, feedsWithErrorStrings, feedsWithoutErrorStrings);
    }

    private ArticleMetadata createMockArticleMetadata(int id, boolean isOldArticle) {
        LocalDateTime publishingTime = isOldArticle ? LocalDateTime.now().minusDays(5) : LocalDateTime.now();
        return new ArticleMetadata("" + id, "Article " + id, null, null, null, null, publishingTime);
    }

    @Ignore
    public void importLimitIsConsidered() {
        rssFeedsImporter.importCommentsFromNewArticles();
        verify(articleRepository, times(6)).existsById(any());
    }

    @Test
    public void feedFetcherWasCalled() {
        rssFeedsImporter.importCommentsFromNewArticles();
        verify(feedFetcher, times(1)).fetchNewArticlesFromFeeds();
    }

    @Test
    public void commentImporterWasCalled() throws Exception {
        rssFeedsImporter.importCommentsFromNewArticles();
        verify(commentImporter, times(4)).importCommentsForArticle(any(), eq(1000));
    }

    @Test
    public void importReportSenderWasCalled() throws CannotSendNotificationToMicrosoftTeams {
        rssFeedsImporter.importCommentsFromNewArticles();
        verify(importReportSender, times(1)).sendImportReport(any(), anyList());
    }

    @Test
    public void amountOfImportedArticlesIsCorrect() {
        List<ArticleImportResult> result = rssFeedsImporter.importCommentsFromNewArticles();
        assertThat(result).hasSize(6);
    }

    @Ignore
    public void oldArticleThatIsNotInDatabaseIsImported() { //4
        List<ArticleImportResult> result = rssFeedsImporter.importCommentsFromNewArticles();
        ArticleImportResult oldArticle = result.stream().filter(article -> article.getArticleMetadata().getArticleId().equals("4")).findAny().get();

        assertThat(oldArticle.isError()).isFalse();
        assertThat(oldArticle.isSkippedBecauseAlreadyImported()).isFalse();
        assertThat(oldArticle.isSkippedBecauseLimitReached()).isFalse();
    }

    @Test
    public void oldArticleThatIsInDatabaseIsNotImported() { //5
        List<ArticleImportResult> result = rssFeedsImporter.importCommentsFromNewArticles();
        ArticleImportResult oldArticle = result.stream().filter(article -> article.getArticleMetadata().getArticleId().equals("5")).findAny().get();

        assertThat(oldArticle.isError()).isFalse();
        assertThat(oldArticle.isSkippedBecauseAlreadyImported()).isTrue();
        assertThat(oldArticle.isSkippedBecauseLimitReached()).isFalse();
    }

    @Ignore
    public void newArticleThatHasCommentsDisabledIsNotImported() {
        List<ArticleImportResult> result = rssFeedsImporter.importCommentsFromNewArticles();
        ArticleImportResult newArticle = result.stream().filter(article -> article.getArticleMetadata().getArticleId().equals("6")).findAny().get();

        assertThat(newArticle.isError()).isFalse();
        assertThat(newArticle.isSkippedBecauseAlreadyImported()).isTrue();
        assertThat(newArticle.isSkippedBecauseLimitReached()).isFalse();
    }

}

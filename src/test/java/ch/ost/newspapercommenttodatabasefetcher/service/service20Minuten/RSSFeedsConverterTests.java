package ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten;

import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotParseRSSFeedException;
import ch.ost.newspapercommenttodatabasefetcher.helper.DateHelper;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleMetadata;
import ch.ost.newspapercommenttodatabasefetcher.model.RSSFeedResult;
import ch.ost.newspapercommenttodatabasefetcher.service.RSSFeedFetcher;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RSSFeedsConverterTests {

    RSSFeedsConverter rssFeedsConverter;

    @Mock
    RSSFeedsPreparer rssFeedsPreparer;

    DateHelper dateHelper = new DateHelper();

    String baseUrl = "https://www.newspaper-base-url.ch";

    @Mock
    RSSFeedFetcher feedFetcher;


    @BeforeEach
    void init() throws CannotParseRSSFeedException {
        rssFeedsConverter = new RSSFeedsConverter(rssFeedsPreparer, dateHelper, feedFetcher, baseUrl);
        Mockito.when(rssFeedsPreparer.getFeedUrlsWithCategories()).thenReturn(createFeedsWithCategories());
        Mockito.when(feedFetcher.fetchArticlesForFeed(baseUrl + "/Schweiz")).thenReturn(createExampleSyndEntriesSwiss());
        Mockito.when(feedFetcher.fetchArticlesForFeed(baseUrl + "/Ausland")).thenReturn(createExampleSyndEntriesInternational());
    }

    @Test
    public void conversionOfFeedsHasNoErrors() {
        RSSFeedResult result = rssFeedsConverter.fetchNewArticlesFromFeeds();
        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    public void duplicateArticlesAreMerged() {
        RSSFeedResult result = rssFeedsConverter.fetchNewArticlesFromFeeds();
        assertThat(result.getArticles()).hasSize(3);
    }

    @Test
    public void feedsWereExtractedCorrectly() {
        RSSFeedResult result = rssFeedsConverter.fetchNewArticlesFromFeeds();
        assertThat(result.getFeedsWithoutErrors()).containsAll(List.of(baseUrl + "/Schweiz", baseUrl + "/Ausland"));
    }

    @Test
    public void hasNoFeedWithErrors() {
        RSSFeedResult result = rssFeedsConverter.fetchNewArticlesFromFeeds();
        assertThat(result.getFeedsWithErrors()).hasSize(0);
    }

    @Test
    public void topicWereMerged() {
        RSSFeedResult result = rssFeedsConverter.fetchNewArticlesFromFeeds();
        assertThat(result.getArticles().stream().anyMatch(entry -> entry.getTopics().size() == 3)).isTrue();
    }

    @Test
    public void articleAttributesAreCorrectlyMapped() {
        RSSFeedResult result = rssFeedsConverter.fetchNewArticlesFromFeeds();
        ArticleMetadata metadata = result.getArticles().stream().findFirst().get();

        assertThat(metadata.getArticleId()).isEqualTo("9012");
        assertThat(metadata.getArticleHeadline()).isEqualTo("Artikelbeschreibung 3");
        assertThat(metadata.getArticleFullLink()).isEqualTo("https://www.newspaper-base-url.ch/9012");
        assertThat(metadata.getArticleShortLink()).isEqualTo("https://www.newspaper-base-url.ch/example-link/9012");
    }

    @Test
    public void articleTopicsAreCorrectlyMapped() {
        RSSFeedResult result = rssFeedsConverter.fetchNewArticlesFromFeeds();
        ArticleMetadata metadata = result.getArticles().stream().findFirst().get();
        assertThat(metadata.getTopics()).contains("Ausland", "International");
    }

    @Test
    public void articlePublicationDateIsCorrectlyConverted() {
        RSSFeedResult result = rssFeedsConverter.fetchNewArticlesFromFeeds();
        LocalDateTime publicationDate = result.getArticles().stream().findFirst().get().getArticlePublicationDate();
        assertThat(publicationDate.getYear()).isEqualTo(2022);
        assertThat(publicationDate.getMonth()).isEqualTo(Month.MARCH);
        assertThat(publicationDate.getDayOfMonth()).isEqualTo(25);
    }

    @Test
    public void faultyFeedGetsAddedToList() throws CannotParseRSSFeedException {

        Mockito.when(feedFetcher.fetchArticlesForFeed(baseUrl + "/Schweiz")).thenReturn(createExampleSyndEntriesSwiss());
        Mockito.when(feedFetcher.fetchArticlesForFeed(baseUrl + "/Ausland")).thenThrow(new CannotParseRSSFeedException(new RuntimeException(), baseUrl + "/Ausland"));

        RSSFeedsConverter faultyRssFeedsConverter = new RSSFeedsConverter(rssFeedsPreparer, dateHelper, feedFetcher, baseUrl);
        RSSFeedResult result = faultyRssFeedsConverter.fetchNewArticlesFromFeeds();
        assertThat(result.getFeedsWithErrors()).hasSize(1);
    }

    @Test
    public void someArticlesAreConvertedEvenIfAFeedFails() throws CannotParseRSSFeedException {

        Mockito.when(feedFetcher.fetchArticlesForFeed(baseUrl + "/Schweiz")).thenReturn(createExampleSyndEntriesSwiss());
        Mockito.when(feedFetcher.fetchArticlesForFeed(baseUrl + "/Ausland")).thenThrow(new CannotParseRSSFeedException(new RuntimeException(), baseUrl + "/Ausland"));

        RSSFeedsConverter faultyRssFeedsConverter = new RSSFeedsConverter(rssFeedsPreparer, dateHelper, feedFetcher, baseUrl);
        RSSFeedResult result = faultyRssFeedsConverter.fetchNewArticlesFromFeeds();
        assertThat(result.getArticles()).isNotEmpty();
    }

    @Test
    public void faultyFeedsAreDetected() throws CannotParseRSSFeedException {
        Mockito.when(feedFetcher.fetchArticlesForFeed(baseUrl + "/Schweiz")).thenReturn(createExampleSyndEntriesSwiss());
        Mockito.when(feedFetcher.fetchArticlesForFeed(baseUrl + "/Ausland")).thenThrow(new CannotParseRSSFeedException(new RuntimeException(), baseUrl + "/Ausland"));

        RSSFeedsConverter faultyRssFeedsConverter = new RSSFeedsConverter(rssFeedsPreparer, dateHelper, feedFetcher, baseUrl);
        RSSFeedResult result = faultyRssFeedsConverter.fetchNewArticlesFromFeeds();
        assertThat(result.hasErrors()).isTrue();
    }


    private HashMap<String, Set<String>> createFeedsWithCategories() {
        HashMap<String, Set<String>> feedsWithCategories = new HashMap<>();
        feedsWithCategories.put("/Schweiz", Set.of("Schweiz"));
        feedsWithCategories.put("/Ausland", Set.of("Ausland", "International"));
        return feedsWithCategories;
    }

    private List<SyndEntry> createExampleSyndEntriesSwiss() {
        List<SyndEntry> syndEntries = new ArrayList<>();
        syndEntries.add(createSyndEntry("Testartikel 1", "Artikelbeschreibung 1", "1234"));
        syndEntries.add(createSyndEntry("Testartikel 2", "Artikelbeschreibung 2", "5678"));
        return syndEntries;
    }

    private List<SyndEntry> createExampleSyndEntriesInternational() {
        List<SyndEntry> syndEntries = new ArrayList<>();
        syndEntries.add(createSyndEntry("Testartikel 2", "Artikelbeschreibung 2", "5678"));
        syndEntries.add(createSyndEntry("Testartikel 3", "Artikelbeschreibung 3", "9012"));

        return syndEntries;
    }

    private SyndEntry createSyndEntry(String title, String description, String identifier) {
        SyndEntry entry1 = new SyndEntryImpl();
        SyndContent description1 = new SyndContentImpl();
        description1.setValue(title);
        entry1.setDescription(description1);
        entry1.setPublishedDate(new GregorianCalendar(2022, Calendar.MARCH, 25).getTime());
        entry1.setTitle(description);
        entry1.setUri("https://www.newspaper-base-url.ch/" + identifier);
        entry1.setLink("https://www.newspaper-base-url.ch/example-link/" + identifier);
        return entry1;

    }


}

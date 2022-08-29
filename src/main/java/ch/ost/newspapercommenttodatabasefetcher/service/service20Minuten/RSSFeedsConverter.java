package ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten;

import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotParseRSSFeedException;
import ch.ost.newspapercommenttodatabasefetcher.helper.DateHelper;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleMetadata;
import ch.ost.newspapercommenttodatabasefetcher.model.RSSFeedResult;
import ch.ost.newspapercommenttodatabasefetcher.service.RSSFeedFetcher;
import com.rometools.rome.feed.synd.SyndEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
/**
 * Accesses multiple RSS Feeds to extract a list of articles
 */
public class RSSFeedsConverter {

    public static Logger logger = LoggerFactory.getLogger(RSSFeedsConverter.class);

    private RSSFeedsPreparer rssFeedPreparer;
    private DateHelper dateHelper;
    private RSSFeedFetcher feedFetcher;
    private String baseUrl;

    public RSSFeedsConverter(@Autowired RSSFeedsPreparer rssFeedPreparer, @Autowired DateHelper dateHelper, @Autowired RSSFeedFetcher feedFetcher, @Value("${settings.20minRSS.baseUrl}") String baseUrl) {
        this.rssFeedPreparer = rssFeedPreparer;
        this.dateHelper = dateHelper;
        this.feedFetcher = feedFetcher;
        this.baseUrl = baseUrl;
    }

    /**
     * Loads a list of RSS feeds from the configuration and creates a list of all articles for those feeds
     *
     * @return a object containing a list of articles
     */
    public RSSFeedResult fetchNewArticlesFromFeeds() {
        HashMap<String, Set<String>> feedUrls = rssFeedPreparer.getFeedUrlsWithCategories();
        logger.info(feedUrls.size() + " feeds found to import");
        return getArticlesFromFeeds(feedUrls);
    }

    private RSSFeedResult getArticlesFromFeeds(HashMap<String, Set<String>> feedUrls) {
        HashMap<String, ArticleMetadata> articles = new HashMap<>();
        Set<String> feedsWithErrors = new HashSet<>();
        Set<String> feedsWithoutErrors = new HashSet<>();

        for (Map.Entry<String, Set<String>> entry : feedUrls.entrySet()) {
            String feedPath = entry.getKey();
            Set<String> feedTopics = entry.getValue();
            String feedUrl = baseUrl + feedPath;

            logger.info("Starting import of feed: " + feedUrl);

            boolean success = tryGetArticlesFromFeed(articles, feedTopics, feedUrl);
            if (success) {
                feedsWithoutErrors.add(feedUrl);
            } else {
                feedsWithErrors.add(feedUrl);
            }
        }

        return new RSSFeedResult(new ArrayList<>(articles.values()), feedsWithErrors, feedsWithoutErrors);
    }

    private boolean tryGetArticlesFromFeed(HashMap<String, ArticleMetadata> articles, Set<String> feedTopics, String feedUrl) {
        try {
            getArticlesFromFeed(articles, feedTopics, feedUrl);
            return true;
        } catch (CannotParseRSSFeedException e) {
            logger.error("Feed with error: " + feedUrl, e);
            return false;
        }
    }

    private void getArticlesFromFeed(HashMap<String, ArticleMetadata> articles, Set<String> feedTopics, String feedUrl) throws CannotParseRSSFeedException {
        Set<ArticleMetadata> articleMetadata;
        articleMetadata = getArticleMetadataForFeed(feedUrl);
        logger.info("Found " + articleMetadata.size() + " articles for this feed");

        mergeNewArticlesWithExistingArticlesToIncludeMultipleTopics(articles, feedTopics, articleMetadata);
    }

    private void mergeNewArticlesWithExistingArticlesToIncludeMultipleTopics(HashMap<String, ArticleMetadata> articles, Set<String> feedTopics, Set<ArticleMetadata> articleMetadata) {
        for (ArticleMetadata currentArticle : articleMetadata) {
            ArticleMetadata newArticle = articles.getOrDefault(currentArticle.getArticleId(), currentArticle);
            newArticle.getTopics().addAll(feedTopics);
            articles.put(newArticle.getArticleId(), newArticle);
        }
    }

    private Set<ArticleMetadata> getArticleMetadataForFeed(String feedUrl) throws CannotParseRSSFeedException {
        List<SyndEntry> entries = feedFetcher.fetchArticlesForFeed(feedUrl);
        return entries.stream().map(entry -> convertSyndEntryToArticleMetadata(entry)).collect(Collectors.toSet());
    }


    private ArticleMetadata convertSyndEntryToArticleMetadata(SyndEntry entry) {
        String articleId = getArticleIdFromArticleUrl(entry.getLink());
        String articleHeadline = entry.getTitle();
        String articleFullLink = entry.getUri();
        String articleShortLink = entry.getLink();
        String articleDescription = entry.getDescription().getValue();
        LocalDateTime articlePublicationDate = dateHelper.convertToLocalDateTimeViaInstant(entry.getPublishedDate());

        return new ArticleMetadata(articleId, articleHeadline, articleFullLink, articleShortLink, articleDescription, new HashSet<>(), articlePublicationDate);
    }

    private static String getArticleIdFromArticleUrl(String uri) {
        String[] splittedUrl = uri.split("/");
        return splittedUrl[splittedUrl.length - 1];
    }

}

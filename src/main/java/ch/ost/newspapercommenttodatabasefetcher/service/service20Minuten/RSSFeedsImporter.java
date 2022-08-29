package ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten;

import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotSendNotificationToMicrosoftTeams;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleImportResult;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleMetadata;
import ch.ost.newspapercommenttodatabasefetcher.model.RSSFeedResult;
import ch.ost.newspapercommenttodatabasefetcher.repository.ArticleRepository;
import ch.ost.newspapercommenttodatabasefetcher.service.ImportReportSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
/**
 * Imports comments for articles from one or multiple RSS Feeds into the database
 */
public class RSSFeedsImporter {

    Logger logger = LoggerFactory.getLogger(RSSFeedsImporter.class);

    public int commentsLimit;

    public int maximumAmountOfArticlesToImportAtOnce;

    public int amountOfHoursAfterPublicationUntilArticleCommentsShouldNotBeImportedPeriodicallyAnymore;

    CommentImporter commentImporter;

    RSSFeedsConverter feedFetcher;

    ImportReportSender importReportSender;

    public RSSFeedsImporter(@Value("${settings.20min.commentPerArticleLimit}") int commentsLimit, @Value("${settings.20min.maximumAmountOfArticlesToImportAtOnce}") int maximumAmountOfArticlesToImportAtOnce, @Value("${settings.20min.periodicallyUpdateCommentsForArticlesPublishedInTheLastFewHours}") int amountOfHoursAfterPublicationUntilArticleCommentsShouldNotBeImportedPeriodicallyAnymore, @Autowired CommentImporter commentImporter, @Autowired RSSFeedsConverter feedFetcher, @Autowired ImportReportSender importReportSender) {
        this.commentsLimit = commentsLimit;
        this.maximumAmountOfArticlesToImportAtOnce = maximumAmountOfArticlesToImportAtOnce;
        this.amountOfHoursAfterPublicationUntilArticleCommentsShouldNotBeImportedPeriodicallyAnymore = amountOfHoursAfterPublicationUntilArticleCommentsShouldNotBeImportedPeriodicallyAnymore;
        this.commentImporter = commentImporter;
        this.feedFetcher = feedFetcher;
        this.importReportSender = importReportSender;
    }

    /**
     * Periodically import new articles and comments from 20 Minuten into the database
     *
     * @return a List of imported or skipped articles and articles that could not be imported because of an error
     */
    @Scheduled(cron="0 0 1,5,9,13,17,21 * * ?") //every day at 1:00, 5:00, 9:00, 13:00, 17:00, 21:00
    public List<ArticleImportResult> importCommentsFromNewArticles() {
        logger.info("\n\n===================");
        logger.info("Starting Import of new Articles");
        RSSFeedResult feedResult = feedFetcher.fetchNewArticlesFromFeeds();
        ArrayList<ArticleMetadata> articlesToImport = feedResult.getArticles();
        List<ArticleImportResult> importResults = importCommentsFromFeeds(articlesToImport);
        try {
            importReportSender.sendImportReport(feedResult, importResults);
        } catch (CannotSendNotificationToMicrosoftTeams e) {
            e.printStackTrace();
        }
        logger.info("import finished");
        return importResults;
    }

    private List<ArticleImportResult> importCommentsFromFeeds(ArrayList<ArticleMetadata> articlesToImport) {

        orderByPublicationDate(articlesToImport);

        List<ArticleImportResult> articleImportResults = new ArrayList<>();
        int successfullyImportedArticles = 0;

        for (ArticleMetadata articleToImport : articlesToImport) {
            boolean shouldSkipArticle = successfullyImportedArticles >= maximumAmountOfArticlesToImportAtOnce;
            ArticleImportResult articleImportResult = tryToImportArticle(articleToImport, shouldSkipArticle);
            articleImportResults.add(articleImportResult);
            if (articleImportResult.isError()) {
                logger.warn("Comments import finished with error");
            }
            if (!articleImportResult.isSkippedOrError()) {
                successfullyImportedArticles++;
            }
        }
        return articleImportResults;
    }

    private void orderByPublicationDate(ArrayList<ArticleMetadata> articlesToImport) {
        articlesToImport.sort((a, b) -> b.getArticlePublicationDate().compareTo(a.getArticlePublicationDate()));
    }

    private ArticleImportResult tryToImportArticle(ArticleMetadata articleMetadata, boolean shouldSkipArticle) {
        ArticleImportResult importResult = new ArticleImportResult(articleMetadata);

        if (shouldSkipArticle) {
            importResult.setSkippedBecauseLimitReached(true);
            return importResult;
        }

        if (isOldAndAlreadyImported(articleMetadata)) {
            importResult.setSkippedBecauseAlreadyImported(true);
            return importResult;
        }

        try {
            int amountOfImportedComments = commentImporter.importCommentsForArticle(articleMetadata, commentsLimit);
            importResult.setAmountOfCommentsImported(amountOfImportedComments);
        } catch (Exception e) {
            logger.error("Cannot import article: " + articleMetadata.getArticleId(), e);
            importResult.setError(true);
        }

        return importResult;
    }


    private boolean isOldAndAlreadyImported(ArticleMetadata articleMetadata) {
        long hoursSincePublication = ChronoUnit.HOURS.between(articleMetadata.getArticlePublicationDate(), LocalDateTime.now());
        return !articleIsNewlyPublished(hoursSincePublication);
        /*return (articleIsAlreadyImported(articleMetadata)
                && (!articleIsNewlyPublished(hoursSincePublication)
                || !existingArticleHasCommentsEnabled(articleMetadata)));*/
    }

    /*private Boolean existingArticleHasCommentsEnabled(ArticleMetadata articleMetadata) {
        try {
            return articleRepository.findById(articleMetadata.getArticleId()).get().getCommentsEnabled();
        } catch (Exception e) {
            logger.error("Cannot find out if article has comments enabled" + articleMetadata.getArticleId());
            return true;
        }
    }*/

    private boolean articleIsNewlyPublished(long hoursSincePublication) {
        return hoursSincePublication < amountOfHoursAfterPublicationUntilArticleCommentsShouldNotBeImportedPeriodicallyAnymore;
    }

    /*private boolean articleIsAlreadyImported(ArticleMetadata articleMetadata) {
        return articleRepository.existsById(articleMetadata.getArticleId());
    }*/
}





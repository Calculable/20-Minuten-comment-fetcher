package ch.ost.newspapercommenttodatabasefetcher.service;

import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotSendNotificationToMicrosoftTeams;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleImportResult;
import ch.ost.newspapercommenttodatabasefetcher.model.RSSFeedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
/**
 * Sends a report for the import of new comments to a microsoft teams webhook
 */
public class ImportReportSender {

    @Value("${settings.importReport.webhookUrl}")
    String webhookUrl;

    @Autowired
    MicrosoftTeamsNotificationSender microsoftTeamsNotificationSender;

    /**
     * Converts the report for the import of new comments into a strings and sends it as a notification to a microsoft teams channel
     *
     * @param feedResult           a report about the imported feeds
     * @param articleImportResults a report about the imported articles
     * @throws CannotSendNotificationToMicrosoftTeams if the report cannot be sent to a Microsoft Teams Channel
     */
    public void sendImportReport(RSSFeedResult feedResult, List<ArticleImportResult> articleImportResults) throws CannotSendNotificationToMicrosoftTeams {
        boolean hadAnyErrors = hadAnyErrors(feedResult, articleImportResults);
        String importReportHeader = convertImportReportHeaderToString(feedResult, articleImportResults);
        String importReportBody = convertImportReportBodyToString(feedResult, articleImportResults);
        String importReportColor = hadAnyErrors ? "#FF0000" : "#00FF00";

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            microsoftTeamsNotificationSender.sendMicrosoftTeamsNotification(webhookUrl, importReportHeader, importReportBody, importReportColor);
        }


    }

    private String convertImportReportHeaderToString(RSSFeedResult feedResult, List<ArticleImportResult> articleImportResults) {
        boolean hadAnyErrors = hadAnyErrors(feedResult, articleImportResults);
        int totalImportedComments = articleImportResults.stream().mapToInt(ArticleImportResult::getAmountOfCommentsImported).sum();

        StringBuilder importReportStringBuilder = new StringBuilder();
        importReportStringBuilder.append("Import Report: ");
        if (hadAnyErrors) {
            importReportStringBuilder.append("Errors occured. ");
        } else {
            importReportStringBuilder.append("Success. ");
        }
        importReportStringBuilder.append(totalImportedComments + " new comments/reactions stored.");
        return importReportStringBuilder.toString();
    }

    private String convertImportReportBodyToString(RSSFeedResult feedResult, List<ArticleImportResult> articleImportResults) {

        String feedResultDescription = feedResult.toString();
        String articleImportDescription =
                articleImportResults.stream()
                        .filter(article -> !article.isSkippedBecauseLimitReached())
                        .map(ArticleImportResult::toString)
                        .collect(Collectors.joining("\n\n"));

        StringBuilder importReportStringBuilder = new StringBuilder();

        importReportStringBuilder.append(feedResultDescription);
        importReportStringBuilder.append("      \n\n");

        importReportStringBuilder.append(articleImportDescription);
        return importReportStringBuilder.toString();
    }

    private boolean hadAnyErrors(RSSFeedResult feedResult, List<ArticleImportResult> articleImportResults) {
        return feedResult.hasErrors() || articleImportResults.stream().anyMatch(ArticleImportResult::isError);
    }
}

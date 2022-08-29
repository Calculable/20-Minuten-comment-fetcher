package ch.ost.newspapercommenttodatabasefetcher.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
/**
 * This model represents a report for a single article after import
 */
public class ArticleImportResult {

    private ArticleMetadata articleMetadata;

    private boolean skippedBecauseAlreadyImported = false;
    private boolean skippedBecauseLimitReached = false;
    private boolean error = false;
    private int amountOfCommentsImported = 0;

    public ArticleImportResult(ArticleMetadata articleMetadata) {
        this.articleMetadata = articleMetadata;
    }

    @Override
    public String toString() {
        return articleMetadata.getArticleId() + " (" + articleStatus() + ")";
    }

    public boolean isSkippedOrError() {
        return skippedBecauseAlreadyImported || skippedBecauseLimitReached || error;
    }

    private String articleStatus() {
        if (error) {
            return "error during import";
        } else if (skippedBecauseAlreadyImported) {
            return "skipped re-import (old article)";
        } else if (skippedBecauseLimitReached) {
            return "skipped because limit of articles reached";
        } else {
            return "imported " + amountOfCommentsImported + " comments/reactions";
        }
    }
}

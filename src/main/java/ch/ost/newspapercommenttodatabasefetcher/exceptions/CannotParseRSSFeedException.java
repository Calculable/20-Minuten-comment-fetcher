package ch.ost.newspapercommenttodatabasefetcher.exceptions;

/**
 * a CannotParseRSSFeedException is thrown, if an RSS feed cannot be parsed
 */
public class CannotParseRSSFeedException extends Exception {

    private String feedUrl;

    public CannotParseRSSFeedException(Throwable cause, String feedUrl) {
        super(cause);
        this.feedUrl = feedUrl;
    }

    public String getFeedUrl() {
        return feedUrl;
    }
}

package ch.ost.newspapercommenttodatabasefetcher.exceptions;

/**
 * a CannotFetchFromApiException is thrown, if an Api cannot be fetched
 */
public class CannotFetchFromApiException extends Exception {

    public CannotFetchFromApiException(Throwable cause) {
        super(cause);
    }
}

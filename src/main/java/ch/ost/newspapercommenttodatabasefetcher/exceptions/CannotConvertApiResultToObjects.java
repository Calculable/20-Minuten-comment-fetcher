package ch.ost.newspapercommenttodatabasefetcher.exceptions;

/**
 * a CannotConvertApiResultToObjects is thrown if the ApiResult returned is not compatible with the Java representation.
 * This might be the case if the Api has changed in the meantime
 */
public class CannotConvertApiResultToObjects extends Exception {
    public CannotConvertApiResultToObjects(Throwable cause) {
        super(cause);
    }
}

package ch.ost.newspapercommenttodatabasefetcher.helper;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
/**
 * Contains helper methods for the handling of strings
 */
public class StringHelper {
    /**
     * Concatenates a list of strings using a comma. A default text is returned if the collection of strings is empty
     *
     * @param strings     a list of Strings to concatenate
     * @param defaultText a default text to return if the list of strings is empty
     * @return a concatenated string, separated by comma
     */
    public String joinStringListOrDefault(Collection<String> strings, String defaultText) {
        if (strings.isEmpty()) {
            return defaultText;
        } else {
            return String.join(", ", strings);
        }
    }
}

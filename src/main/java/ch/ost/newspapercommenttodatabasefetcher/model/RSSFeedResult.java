package ch.ost.newspapercommenttodatabasefetcher.model;

import ch.ost.newspapercommenttodatabasefetcher.helper.StringHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
@Getter
/**
 * This model contains a report for the fetching of multiple RSS Feeds
 */
public class RSSFeedResult {

    private ArrayList<ArticleMetadata> articles;
    private Set<String> feedsWithErrors;
    private Set<String> feedsWithoutErrors;

    public boolean hasErrors() {
        return !feedsWithErrors.isEmpty();
    }

    @Override
    public String toString() {
        StringHelper stringHelper = new StringHelper();

        return String.format(
                "Feeds with errors:" +
                        "\n" +
                        "%s" +
                        "\n" +
                        "\n" +
                        "Feeds without errors: \n" +
                        "%s", stringHelper.joinStringListOrDefault(feedsWithErrors, "(none)"), stringHelper.joinStringListOrDefault(feedsWithoutErrors, "(none)"));
    }


}

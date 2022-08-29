package ch.ost.newspapercommenttodatabasefetcher.service;

import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotParseRSSFeedException;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;

@Service
/**
 * Fetches an RSS Feed
 */
public class RSSFeedFetcher {

    /**
     * Fetches the articles from an RSS feed using the Rometools library
     *
     * @param feedUrl the url of the feed
     * @return a list of articles
     * @throws CannotParseRSSFeedException if the RSS feed cannot be accessed
     */
    public List<SyndEntry> fetchArticlesForFeed(String feedUrl) throws CannotParseRSSFeedException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed;

        try {
            feed = input.build(new XmlReader(new URL(feedUrl)));
        } catch (Exception e) {
            throw new CannotParseRSSFeedException(e, feedUrl);
        }

        return feed.getEntries();
    }

}

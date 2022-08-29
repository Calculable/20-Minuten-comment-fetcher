package ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

@Service
/**
 * Creates a list of RSS Feeds, depending on the configuration chosen.
 */
public class RSSFeedsPreparer {


    @Value("#{new Boolean('${settings.20minRSS.includeHotTopicArticles}')}")
    private Boolean includeHotTopicArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeCommercialArticles}')}")
    private Boolean includeCommercialArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeNationalArticles}')}")
    private Boolean includeNationalArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeLocalArticles}')}")
    private Boolean includeLocalArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeRegionArticles}')}")
    private Boolean includeRegionArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeSportArticles}')}")
    private Boolean includeSportArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeInternationalArticles}')}")
    private Boolean includeInternationalArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeEconomyArticles}')}")
    private Boolean includeEconomyArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeOneLoveArticles}')}")
    private Boolean includeOneLoveArticles;

    @Value("#{new Boolean('${settings.20minRSS.includePeopleArticles}')}")
    private Boolean includePeopleArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeMusicArticles}')}")
    private Boolean includeMusicArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeMovieArticles}')}")
    private Boolean includeMovieArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeCommunityArticles}')}")
    private Boolean includeCommunityArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeDigitalArticles}')}")
    private Boolean includeDigitalArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeScienceArticles}')}")
    private Boolean includeScienceArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeHealthArticles}')}")
    private Boolean includeHealthArticles;

    @Value("#{new Boolean('${settings.20minRSS.includeMyNewsPlusArticles}')}")
    private Boolean includeMyNewsPlusArticles;


    /**
     * Generates a list of 20 Minuten RSS feeds depending on the configuration
     *
     * @return a list of RSS feeds from 20 Minuten
     */
    public HashMap<String, Set<String>> getFeedUrlsWithCategories() {

        HashMap<String, Set<String>> categories = new HashMap<>();

        if (includeHotTopicArticles) {
            categories.put("/ukraine", Set.of("Ukraine"));
            categories.put("/coronavirus", Set.of("Corona Virus"));
        }

        if (includeCommercialArticles) {
            categories.put("/wirsindzukunft/gebaeude", Set.of("Wir sind Zukunft, Gebäude"));
            categories.put("/wirsindzukunft/mobilitaet", Set.of("Wir sind Zukunft, Mobilität"));
            categories.put("/wirsindzukunft/lifestyle", Set.of("Wir sind Zukunft, Lifestyle"));
            categories.put("/wirsindzukunft/bildungundberatung", Set.of("Wir sind Zukunft, Bildung & Beratung"));
            categories.put("/wirsindzukunft/innovationundenergie", Set.of("Wir sind Zukunft, Innovation & Energie"));
            categories.put("/kochen", Set.of("kochen"));
            categories.put("/coopzeitung-weekend", Set.of("coopzeitung-weekend"));
            categories.put("/wettbewerbe", Set.of("wettbewerbe"));
        }

        if (includeNationalArticles) {
            categories.put("/schweiz", Set.of("Schweiz"));
            categories.put("/abstimmungen", Set.of("Abstimmungen"));
        }

        if (includeLocalArticles) {
            categories.put("/zuerich", Set.of("Zürich"));
            categories.put("/bern", Set.of("Bern"));
            categories.put("/basel", Set.of("Basel"));
        }

        if (includeRegionArticles) {
            categories.put("/zentralschweiz", Set.of("Zentralschweiz"));
            categories.put("/ostschweiz", Set.of("Ostschweiz"));
        }

        if (includeSportArticles) {
            categories.put("/sport/fussball", Set.of("Sport, Fussball"));
            categories.put("/sport/super-league", Set.of("Super League"));
            categories.put("/sport/challenge-league", Set.of("Challenge League"));
            categories.put("/sport/eishockey", Set.of("Eishockey"));
            categories.put("/sport/wintersport", Set.of("Wintersport"));
            categories.put("/sport/tennis", Set.of("Tennis"));
            categories.put("/sport/motorsport", Set.of("Motorsport"));
            categories.put("/sport/weitere-sportarten", Set.of("Weitere Sportarten"));
            categories.put("/e-sport", Set.of("E-Sport"));
        }

        if (includeInternationalArticles) {
            categories.put("/ausland", Set.of("ausland"));
        }

        if (includeEconomyArticles) {
            categories.put("/wirtschaft", Set.of("wirtschaft"));
        }

        if (includeOneLoveArticles) {
            categories.put("/onelove", Set.of("OneLove"));
        }

        if (includePeopleArticles) {
            categories.put("/people", Set.of("People"));
        }

        if (includeMovieArticles) {
            categories.put("/kinostreaming", Set.of("Kino & Streaming"));
            categories.put("/influencer-radar", Set.of("Influencer"));
        }

        if (includeMusicArticles) {
            categories.put("/best-crushing-newcomer", Set.of("Best Crushing Newcomer"));
        }

        if (includeCommunityArticles) {
            categories.put("/community", Set.of("Community"));
        }

        if (includeDigitalArticles) {
            categories.put("/digital", Set.of("Digital"));
        }

        if (includeScienceArticles) {
            categories.put("/wissen", Set.of("Wissen"));
        }

        if (includeHealthArticles) {
            categories.put("/gesundheit", Set.of("Gesundheit"));
        }

        if (includeMyNewsPlusArticles) {
            categories.put("/my-news-plus", Set.of("My News Plus"));
        }

        return categories;
    }
}

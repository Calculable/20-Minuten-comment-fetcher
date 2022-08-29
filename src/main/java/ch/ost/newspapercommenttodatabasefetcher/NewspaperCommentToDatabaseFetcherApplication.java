package ch.ost.newspapercommenttodatabasefetcher;

import ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten.RSSFeedsImporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
/**
 * Main Class. Starts Spring Boot and the Spring Scheduler to starts the periodic import of new comments and articles into the database.
 */
public class NewspaperCommentToDatabaseFetcherApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(NewspaperCommentToDatabaseFetcherApplication.class, args);
    }
}

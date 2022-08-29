package ch.ost.newspapercommenttodatabasefetcher.repository;

import ch.ost.newspapercommenttodatabasefetcher.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NewspaperRepositoryTests {

    @Autowired
    private NewspaperRepository newspaperRepository;

    @Test
    void contextLoads() {
    }

    @Test
    public void testDatabaseIsEmpty() {
        Iterable<Newspaper> newspapers = newspaperRepository.findAll();
        assertThat(newspapers).hasSize(0);
    }

    @Test
    public void newspaperCanBePersisted() {
        Newspaper newspaper = new Newspaper();
        newspaper.setName("Demo Usepaper");
        newspaper.setLanguageCode("de");
        newspaperRepository.save(newspaper);
        Iterable<Newspaper> newspapers = newspaperRepository.findAll();
        assertThat(newspapers).hasSize(1);
    }

    @Test
    public void duplicateNewspaperIsOnlySavedOnce() {
        Newspaper newspaper = new Newspaper();
        newspaper.setName("Demo Newspaper");
        newspaper.setLanguageCode("de");

        Newspaper duplicateNewspaper = new Newspaper();
        duplicateNewspaper.setName("Demo Newspaper");
        duplicateNewspaper.setLanguageCode("de");

        newspaperRepository.saveAll(List.of(newspaper, duplicateNewspaper));

        Iterable<Newspaper> newspapers = newspaperRepository.findAll();
        assertThat(newspapers).hasSize(1);
    }

    @Test
    public void persistCascadeWorks() {
        Newspaper newspaper = new Newspaper();
        newspaper.setName("Demo Newspaper");
        newspaper.setLanguageCode("de");

        Article article1 = new Article();
        article1.setId("articleID");
        article1.setHeadline("demo headline");
        article1.setFullLink("https://www.full-link-to-article.ch");

        Comment comment1 = new Comment();
        comment1.setId("commentID");
        comment1.setContent("Demo Comment");
        comment1.setCreatedAt(LocalDateTime.now());

        CommentAuthor commentAuthor1 = new CommentAuthor();
        commentAuthor1.setUsername("Demo Comment Author");

        Reaction commentReaction = new Reaction();
        commentReaction.setAmountOfNegativeReactions(1);
        commentReaction.setAmountOfPositiveReactions(1);

        comment1.setCommentAuthor(commentAuthor1);
        comment1.setReactions(List.of(commentReaction));
        article1.setComments(List.of(comment1));
        newspaper.setArticles(List.of(article1));

        newspaperRepository.save(newspaper);
        Newspaper fetchedNewspaper = newspaperRepository.findById("Demo Newspaper").get();

        assertThat(fetchedNewspaper.getArticles()).hasSize(1);
        assertThat(fetchedNewspaper.getArticles().get(0).getComments()).hasSize(1);
        assertThat(fetchedNewspaper.getArticles().get(0).getComments().get(0).getCommentAuthor()).isNotNull();
        assertThat(fetchedNewspaper.getArticles().get(0).getComments().get(0).getReactions()).hasSize(1);
    }

    @Test
    public void firstStoredAtAttributeIsAutomaticallySet() {
        Newspaper newspaper = new Newspaper();
        newspaper.setName("Demo Newspaper");
        newspaper.setLanguageCode("de");

        Article article1 = new Article();
        article1.setId("articleID");
        article1.setHeadline("demo headline");
        article1.setFullLink("https://www.full-link-to-article.ch");

        newspaper.setArticles(List.of(article1));

        newspaperRepository.save(newspaper);
        Newspaper fetchedNewspaper = newspaperRepository.findById("Demo Newspaper").get();

        assertThat(fetchedNewspaper.getFirstStoredAt()).isNotNull();
        assertThat(fetchedNewspaper.getArticles().get(0).getFirstStoredAt()).isNotNull();
    }

}

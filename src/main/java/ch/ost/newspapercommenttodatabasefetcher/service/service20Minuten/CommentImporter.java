package ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten;

import ch.ost.newspapercommenttodatabasefetcher.entity.Newspaper;
import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotConvertApiResultToObjects;
import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotFetchFromApiException;
import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotSaveDatabaseEntities;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleMetadata;
import ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse.ApiResponseCommentList;
import ch.ost.newspapercommenttodatabasefetcher.repository.ArticleRepository;
import ch.ost.newspapercommenttodatabasefetcher.repository.NewspaperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/**
 * Imports comments for a specific article into the database
 */
public class CommentImporter {

    Logger logger = LoggerFactory.getLogger(CommentImporter.class);

    @Autowired
    NewspaperRepository newspaperRepository;

    @Autowired
    CommentFetcher commentFetcher;

    @Autowired
    CommentConverter commentConverter;

    /**
     * Imports the comments for a given article into the database
     *
     * @param articleMetadata metadata about the article to import
     * @param commentsLimit   maximum amount of comments to import
     * @return the amount if comments imported
     * @throws CannotFetchFromApiException     if the api cannot be accessed
     * @throws CannotConvertApiResultToObjects if the response from the api cannot be converted
     */
    public int importCommentsForArticle(ArticleMetadata articleMetadata, int commentsLimit) throws CannotSaveDatabaseEntities, CannotFetchFromApiException, CannotConvertApiResultToObjects {
        logger.info("Starting import of comments article: " + articleMetadata.getArticleFullLink());
        ApiResponseCommentList fetchedComments;
        fetchedComments = commentFetcher.fetchCommentsFromApi(articleMetadata, commentsLimit);
        logger.info("Comments fetched: " + fetchedComments.getTotalCount());
        Newspaper newspaper = commentConverter.convertApiResponseToDatabaseEntities(fetchedComments, articleMetadata);
        logger.info("Comments converted to database entities");
        try {

            newspaperRepository.save(newspaper);
        } catch (Exception e) {
            logger.error("Error while saving entities", e);
            throw new CannotSaveDatabaseEntities(e);
        }

        logger.info("Article and comments sucessfully saved.");
        return fetchedComments.getComments().size();
    }
}

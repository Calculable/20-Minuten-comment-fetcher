package ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten;

import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotConvertApiResultToObjects;
import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotFetchFromApiException;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleMetadata;
import ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse.ApiResponseCommentList;
import ch.ost.newspapercommenttodatabasefetcher.service.ApiStringResultFetcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Service
/**
 * Fetches comments for an article from the 20 Minuten API
 */
public class CommentFetcher {

    private String apiBaseUrl;

    private int apiParamTenantId;

    private String apiParamSortBy;

    private String apiParamSortOrder;

    private ApiStringResultFetcher apiStringResultFetcher;

    public CommentFetcher(@Value("${settings.20min.api.baseUrl}") String apiBaseUrl, @Value("${settings.20min.api.tenantId}") int apiParamTenantId, @Value("${settings.20min.api.sortBy}") String apiParamSortBy, @Value("${settings.20min.api.sortOrder}") String apiParamSortOrder, @Autowired ApiStringResultFetcher apiStringResultFetcher) {
        this.apiBaseUrl = apiBaseUrl;
        this.apiParamTenantId = apiParamTenantId;
        this.apiParamSortBy = apiParamSortBy;
        this.apiParamSortOrder = apiParamSortOrder;
        this.apiStringResultFetcher = apiStringResultFetcher;
    }

    /**
     * Fetches a list of comment from the 20 Minuten Api for a given article
     *
     * @param articleMetadata additional metadata about the article
     * @param commentLimit    maximum amount of comments to fetch
     * @return an object containing the api response with a list of comments
     * @throws CannotFetchFromApiException     if the api cannot be accessed
     * @throws CannotConvertApiResultToObjects if the api response can not be converted
     */
    public ApiResponseCommentList fetchCommentsFromApi(ArticleMetadata articleMetadata, int commentLimit) throws CannotFetchFromApiException, CannotConvertApiResultToObjects {
        URI endpoint = buildEndpointURI(articleMetadata.getArticleId(), commentLimit);
        String apiResponseJson = apiStringResultFetcher.queryApiForJsonResponse(endpoint);
        return convertJsonToObjects(apiResponseJson);
    }

    private URI buildEndpointURI(String articleId, int commentLimit) {
        return UriComponentsBuilder
                .fromHttpUrl(apiBaseUrl)
                .queryParam("tenantId", apiParamTenantId)
                .queryParam("contentId", articleId)
                .queryParam("limit", commentLimit)
                .queryParam("sortBy", apiParamSortBy)
                .queryParam("sortOrder", apiParamSortOrder)
                .build()
                .toUri();
    }

    private ApiResponseCommentList convertJsonToObjects(String apiResponseJson) throws CannotConvertApiResultToObjects {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ApiResponseCommentList result = objectMapper.readValue(apiResponseJson, ApiResponseCommentList.class);
            return result;
        } catch (IOException e) {
            throw new CannotConvertApiResultToObjects(e);
        }
    }

}

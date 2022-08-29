package ch.ost.newspapercommenttodatabasefetcher.service.service20Minuten;

import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotConvertApiResultToObjects;
import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotFetchFromApiException;
import ch.ost.newspapercommenttodatabasefetcher.model.ArticleMetadata;
import ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse.ApiResponseCommentList;
import ch.ost.newspapercommenttodatabasefetcher.service.ApiStringResultFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CommentFetcherTests {

    CommentFetcher commentFetcher;

    @Mock
    ApiStringResultFetcher apiStringResultFetcher;

    @BeforeEach
    void init() {
        commentFetcher = new CommentFetcher("https://api.20min.ch/comment/v1/comments", 6, "created_at", "desc", apiStringResultFetcher);
    }

    @Test
    public void validApiResponseCanBeMappedToObject() throws CannotFetchFromApiException, CannotConvertApiResultToObjects {
        Mockito.when(apiStringResultFetcher.queryApiForJsonResponse(any())).thenReturn("{\"commentingEnabled\":false,\"nextLink\":\"https://api.20min.ch/comment/v1/comments?tenantId=6&contentId=857295187913&sortOrder=desc&sortBy=created_at&limit=2&cursor=2022-03-07T09%3A55%3A52.999Z\",\"comments\":[{\"id\":\"6225d95124f5ca0012b967ab\",\"authorNickname\":\"gottfridstutz\",\"authorAvatar\":{\"light\":\"https://api.20min.ch/user/static/13@4x.08bad1b72b0478ac1ad8b418bc60e1fac31488d5c1622f246995fb85df79c83a.png\",\"dark\":\"https://api.20min.ch/user/static/13@4x.08bad1b72b0478ac1ad8b418bc60e1fac31488d5c1622f246995fb85df79c83a.png\"},\"body\":\"Die wollten doch nur selber einen Straich spielen ......\",\"createdAt\":\"2022-03-07T10:07:13.635Z\",\"reactions\":{\"awesome\":13,\"bad\":7,\"nonsense\":7,\"unnecessary\":42,\"smart\":1,\"exact\":20},\"replies\":[]},{\"id\":\"6225d6a8d70c7800124c1d4f\",\"authorNickname\":\"Denk mal\",\"authorAvatar\":{\"light\":\"https://api.20min.ch/user/static/8@4x.269fe50fb58a51d1e94a235c2ebbf4a83666672f27edccf22cfc020537011935.png\",\"dark\":\"https://api.20min.ch/user/static/8@4x.269fe50fb58a51d1e94a235c2ebbf4a83666672f27edccf22cfc020537011935.png\"},\"body\":\"zum Glück haben wir auf der Welt sonst keine Probleme. Schön wird über solche Luxusprobleme berichtet. Viele Menschen wäre froh, wenn sie überhaupt Licht hätten. \",\"createdAt\":\"2022-03-07T09:55:52.999Z\",\"reactions\":{\"awesome\":51,\"bad\":48,\"nonsense\":87,\"unnecessary\":156,\"smart\":4,\"exact\":540},\"replies\":[{\"id\":\"6225dff4d749f80012ba58f5\",\"authorNickname\":\"el_bruto\",\"authorAvatar\":{\"light\":\"https://api.20min.ch/user/static/15@4x.1a9d6907bd8a7b66b15d3dc47dd8533469c08e25431f80e0a865d29ce4753ebc.png\",\"dark\":\"https://api.20min.ch/user/static/15@4x.1a9d6907bd8a7b66b15d3dc47dd8533469c08e25431f80e0a865d29ce4753ebc.png\"},\"body\":\"@Denk mal  Andere Frage: warum muss in einem Geschäft, das Nachts ja geschlossen hat, überhaupt Licht an sein?\",\"createdAt\":\"2022-03-07T10:35:32.499Z\",\"reactions\":{\"exact\":54,\"nonsense\":6,\"awesome\":15,\"smart\":1,\"unnecessary\":1}},{\"id\":\"6225e34b24f5ca0012ba794d\",\"authorNickname\":\"MindyDüsentrieb\",\"authorAvatar\":{\"light\":\"https://api.20min.ch/user/static/12@4x.fd9d633baf5d21b01958e2ea8b2d1f6923f3db99e38f85b28333985673cdf482.png\",\"dark\":\"https://api.20min.ch/user/static/12@4x.fd9d633baf5d21b01958e2ea8b2d1f6923f3db99e38f85b28333985673cdf482.png\"},\"body\":\"@Denk mal Du hast ja Recht. Trotzdem, wenn man den Morgenstreich nicht kennt oder liebt, kann man das nicht verstehen.....  Also, erst überlegen, dann motzen!\",\"createdAt\":\"2022-03-07T10:49:47.310Z\",\"reactions\":{\"nonsense\":75,\"exact\":75,\"smart\":2,\"unnecessary\":17,\"awesome\":10,\"bad\":13}}]}],\"totalCount\":110}");
        ArticleMetadata articleMetadata = new ArticleMetadata("articleId", "Headline", "https://www.linkToArticle.ch", "https://www.shortlinkToArticle.ch", "description", new HashSet<>(), LocalDateTime.now());
        ApiResponseCommentList result = commentFetcher.fetchCommentsFromApi(articleMetadata, 10);
        assertThat(result.getComments()).hasSize(2);
    }

    @Test
    public void invalidApiResonseThrowsException() throws CannotFetchFromApiException {
        Mockito.when(apiStringResultFetcher.queryApiForJsonResponse(any())).thenReturn("{Unexpected Response}");
        ArticleMetadata articleMetadata = new ArticleMetadata("articleId", "Headline", "https://www.linkToArticle.ch", "https://www.shortlinkToArticle.ch", "description", new HashSet<>(), LocalDateTime.now());
        assertThatThrownBy(() -> {
            commentFetcher.fetchCommentsFromApi(articleMetadata, 10);
        }).isInstanceOf(CannotConvertApiResultToObjects.class);
    }

    @Test
    public void exceptionFromInvalidApiRequestIsRethrown() throws CannotFetchFromApiException {
        Mockito.when(apiStringResultFetcher.queryApiForJsonResponse(any())).thenThrow(new CannotFetchFromApiException(null));
        ArticleMetadata articleMetadata = new ArticleMetadata("articleId", "Headline", "https://www.linkToArticle.ch", "https://www.shortlinkToArticle.ch", "description", new HashSet<>(), LocalDateTime.now());
        assertThatThrownBy(() -> {
            commentFetcher.fetchCommentsFromApi(articleMetadata, 10);
        }).isInstanceOf(CannotFetchFromApiException.class);
    }
}

package ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
/**
 * Representation of a 20 Minuten API response.
 */
public class ApiResponseCommentList {
    Boolean commentingEnabled;
    String nextLink;
    List<ApiResponseComment> comments;
    Integer totalCount;
}
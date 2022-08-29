package ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
/**
 * Representation of a comment in the 20 Minuten API.
 */
public class ApiResponseComment {
    String id;
    String authorNickname;
    ApiResponseCommentAuthorAvatar authorAvatar;
    String body;
    String createdAt;
    ApiResponseCommentReaction reactions;
    List<ApiResponseComment> replies;
}
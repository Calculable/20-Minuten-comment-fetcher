package ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
/**
 * Representation of a comment-author's avater image in the 20 Minuten API.
 */
public class ApiResponseCommentAuthorAvatar {
    String light;
    String dark;
}
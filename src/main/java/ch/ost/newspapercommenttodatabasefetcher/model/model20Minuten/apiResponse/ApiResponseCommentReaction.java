package ch.ost.newspapercommenttodatabasefetcher.model.model20Minuten.apiResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
/**
 * Representation of a comment-reaction in the 20 Minuten API.
 */
public class ApiResponseCommentReaction {
    Integer awesome;
    Integer bad;
    Integer nonsense;
    Integer unnecessary;
    Integer smart;
    Integer exact;
}
package ch.ost.newspapercommenttodatabasefetcher.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Setter
@Getter
/**
 * This model contains metadata about a newspaper article
 */
public class ArticleMetadata {
    private String articleId;
    private String articleHeadline;
    private String articleFullLink;
    private String articleShortLink;
    private String articleDescription;
    private Set<String> topics;
    private LocalDateTime articlePublicationDate;
}

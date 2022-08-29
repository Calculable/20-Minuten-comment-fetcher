package ch.ost.newspapercommenttodatabasefetcher.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table
@EntityListeners(AuditingEntityListener.class)
/**
 * Represents the Newspaper-Table in the Database
 */
public class Newspaper implements Serializable {

    @Id
    String name;

    @Column(nullable = false)
    String languageCode;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "newspaper")
    List<Article> articles;

    @OneToMany(fetch = FetchType.LAZY, cascade = {}, mappedBy = "newspaper")
    Set<CommentAuthor> commentAuthors;

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime firstStoredAt;

    @Column()
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;
}

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

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table
@EntityListeners(AuditingEntityListener.class)
/**
 * Represents the Comments-Table in the Database
 */
public class Comment implements Serializable {

    @Id
    String id;

    @Column(nullable = false, length = 10000)
    String content;

    @Column(nullable = true, length = 1000)
    String headline;

    Boolean isTroll = null;

    Boolean isConflict;

    Boolean isOffending;

    Boolean isSpam;

    Boolean isPossibleFakeNews;

    Boolean isMistrust;

    Boolean isRejected = null;


    @CreatedDate
    @Column(updatable = false)
    LocalDateTime firstStoredAt;

    @Column()
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @ManyToOne(cascade = {})
    @PrimaryKeyJoinColumn
    Article article;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    CommentAuthor commentAuthor;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "comment")
    List<Reaction> reactions;

    //@OneToMany(fetch = FetchType.LAZY, cascade = {}, mappedBy = "parentComment")
    @Transient
    private List<Comment> replies;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Comment parentComment;
}

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table
@EntityListeners(AuditingEntityListener.class)
/**
 * Represents the Article-Table in the Database
 */
public class Article implements Serializable {

    @Id
    String id;

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime firstStoredAt;

    @Column()
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;

    @Column(nullable = true, length = 1000)
    String headline;

    @Column(nullable = true, length = 1000)
    String fullLink;

    @Column(nullable = true, length = 1000)
    String shortLink;

    @Column(nullable = true, length = 4000)
    String description;

    @Column(updatable = false)
    LocalDateTime timeOfPublication;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<Topic> topics = new HashSet<>();

    @Column(nullable = true, updatable = false)
    Boolean commentsEnabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    Newspaper newspaper;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "article")
    List<Comment> comments;

}

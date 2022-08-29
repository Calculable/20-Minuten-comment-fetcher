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
 * Represents the CommentAuthor-Table in the Database
 */
public class CommentAuthor implements Serializable {

    @Id
    String username;

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime firstStoredAt;

    @Column()
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;

    @ManyToOne(cascade = {})
    @PrimaryKeyJoinColumn
    Newspaper newspaper;

    //@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "commentAuthor")
    @Transient
    List<Comment> comments;
}

package ch.ost.newspapercommenttodatabasefetcher.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table
@EntityListeners(AuditingEntityListener.class)
/**
 * Represents the Reaction-Table in the Database
 */
public class Reaction implements Serializable {

    @Id
    @GeneratedValue
    Long id;

    @CreatedDate
    @Column(nullable = false)
    LocalDateTime storedAt;

    @Column(nullable = false)
    Integer amountOfPositiveReactions;

    @Column(nullable = false)
    Integer amountOfNegativeReactions;

    @ManyToOne()
    Comment comment;

}

package net.yahyas.research.WebSearch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "stored_articles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(nullable = false)
    private String source;

    private String author;

    @Column(length = 2000)
    private String summary;

    @Column(columnDefinition = "text")
    private String content;

    private Instant publishedAt;

    @Column(nullable = false, updatable = false)
    private Instant savedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @PrePersist
    private void setSavedAtIfMissing() {
        if (savedAt == null) {
            savedAt = Instant.now();
        }
    }
}

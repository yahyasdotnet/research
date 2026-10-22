package net.yahyas.research.Article.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "article_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(length = 1000)
    private String summary;

    @Column(length = 255)
    private String sourceName;

    @Column(length = 2048)
    private String sourceUrl;

    @Column(length = 2048)
    private String citation;

    @Column
    private Integer wordCount;

    @OneToOne(mappedBy = "articleContent")
    private ArticleMetadata article;

    @PrePersist
    @PreUpdate
    public void updateWordCount() {
        if (body == null || body.isBlank()) {
            wordCount = 0;
            return;
        }

        wordCount = body.trim().split("\\s+").length;
    }
}

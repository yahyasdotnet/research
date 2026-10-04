package net.yahyas.research.WebSearch.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subject")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private String authorName;

    @ElementCollection
    @CollectionTable(name = "subject_keywords", joinColumns = @JoinColumn(name = "subject_id"))
    @Column(name = "keyword", nullable = false)
    @OrderColumn(name = "keyword_order")
    private List<String> keywords = new ArrayList<>();

    private Integer desiredResultCount;

    @ElementCollection
    @CollectionTable(name = "subject_preferred_search_engines", joinColumns = @JoinColumn(name = "subject_id"))
    @Column(name = "search_engine", nullable = false)
    @Enumerated(EnumType.STRING)
    @OrderColumn(name = "engine_order")
    private List<SearchEngine> preferredSearchEngines = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "subject_preferred_sources", joinColumns = @JoinColumn(name = "subject_id"))
    @Column(name = "source", nullable = false)
    @OrderColumn(name = "source_order")
    private List<String> preferredSources = new ArrayList<>();

    @Column(name = "search_constraints", length = 2000)
    private String searchConstraints;
}

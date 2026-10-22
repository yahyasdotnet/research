package net.yahyas.research.websearch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebSearchSource {

    @Column(length = 500)
    private String title;

    @Column(length = 2048)
    private String url;

    @Column(length = 4000)
    private String description;

    private Instant publishedAt;
}

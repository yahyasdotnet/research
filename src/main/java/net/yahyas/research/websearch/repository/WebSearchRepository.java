package net.yahyas.research.websearch.repository;

import java.util.List;
import net.yahyas.research.websearch.model.WebSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebSearchRepository extends JpaRepository<WebSearch, Long> {

    List<WebSearch> findTop20ByOwnerEmailOrderByCreatedAtDesc(String ownerEmail);
}

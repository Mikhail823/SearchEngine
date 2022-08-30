package org.spring.services.repo;

import org.spring.model.Index;

import java.util.List;

public interface IndexRepo {
    List<Index> getAllIndexingByLemmaId(long lemmaId);
    List<Index> getAllIndexingByPageId(long pageId);
    void deleteAllIndexing(List<Index> indexingList);
    Index getIndexing (long lemmaId, long pageId);
    void save(Index indexing);
}

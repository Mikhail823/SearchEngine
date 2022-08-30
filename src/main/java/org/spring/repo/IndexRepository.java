package org.spring.repo;

import org.spring.model.Index;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends CrudRepository<Index, Long> {
    Index findByLemmaIdAndPageId(long lemmaId, long pageId);

    List<Index> findByLemmaId(int lemmaId);

    List<Index> findByPageId(int pageId);

}

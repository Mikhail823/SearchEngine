package org.spring.services.impl;

import org.spring.model.Index;
import org.springframework.stereotype.Service;
import org.spring.repo.IndexRepository;
import org.spring.services.repo.IndexRepo;

import java.util.List;

@Service
public class IndexRepoServiceImpl implements IndexRepo {

    private final IndexRepository indexRepository;

    public IndexRepoServiceImpl(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    @Override
    public List<Index> getAllIndexingByLemmaId(long lemmaId) {
        return indexRepository.findByLemmaId((int)lemmaId);
    }

    @Override
    public List<Index> getAllIndexingByPageId(long pageId) {
        return null;
    }

    @Override
    public synchronized void deleteAllIndexing(List<Index> indexingList){
        indexRepository.deleteAll(indexingList);
    }

    @Override
    public Index getIndexing(long lemmaId, long pageId) {
        Index indexing = null;
        try{
            indexing = indexRepository.findByLemmaIdAndPageId(lemmaId, pageId);
        } catch (Exception e) {
            System.out.println("lemmaId: " + lemmaId + " + pageId: " + pageId + " not unique");
            e.printStackTrace();
        }
        return indexing;
    }

    @Override
    public synchronized void save(Index indexing) {
        indexRepository.save(indexing);
    }

}

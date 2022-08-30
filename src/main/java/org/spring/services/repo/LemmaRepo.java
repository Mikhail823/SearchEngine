package org.spring.services.repo;

import org.spring.model.Index;
import org.spring.model.Lemma;

import java.util.List;

public interface LemmaRepo {
    List<Lemma> getLemma (String lemmaName);
    void save(Lemma lemma);
    long lemmaCount();
    long lemmaCount(long siteId);
    void deleteAllLemmas(List<Lemma> lemmaList);
    List<Lemma> findLemmasByIndexing(List<Index> indexingList);
}

package org.spring.services.repo;

import org.spring.services.responces.ResponseService;

public interface IndexingService {
    ResponseService startIndexingAll();
    ResponseService stopIndexing();
    ResponseService startIndexingOne(String url);
}

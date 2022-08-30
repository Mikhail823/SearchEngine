package org.spring.services.repo;

import org.spring.model.Request;
import org.spring.services.responces.ResponseService;

import java.io.IOException;

public interface SearchService {
    ResponseService getResponse (Request request, String url, int offset, int limit) throws IOException;
}

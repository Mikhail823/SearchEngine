package org.spring.services.repo;

import org.spring.model.Page;

import java.util.Optional;

public interface PageRepo {
    Page getPage (String pagePath);
    void save(Page page);
    Optional<Page> findPageById(long id);
    Optional<Page> findPageByPageIdAndSiteId(int pageId, int siteId);
    long pageCount();
    long pageCount(long siteId);
    void deletePage(Page page);
}

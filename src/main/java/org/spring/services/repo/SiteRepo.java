package org.spring.services.repo;

import org.spring.model.Site;

import java.util.List;

public interface SiteRepo {
    Site getSite (String url);
    Site getSite (int siteId);
    void save(Site site);
    long siteCount();
    List<Site> getAllSites();

}

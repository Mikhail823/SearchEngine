package org.spring.services.impl;

import org.spring.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.spring.repo.PageRepository;
import org.spring.services.repo.PageRepo;

import java.util.Optional;

@Service
public class PageRepoServiceImpl implements PageRepo {

    private final PageRepository pageRepository;
    @Autowired
    public PageRepoServiceImpl(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public Page getPage(String pagePath) {
        return pageRepository.findByPath(pagePath);
    }

    @Override
    public synchronized void save(Page page) {
        pageRepository.save(page);
    }

    @Override
    public Optional<Page> findPageById(long id) {
        return pageRepository.findById(id);
    }

    @Override
    public Optional<Page> findPageByPageIdAndSiteId(int pageId, int siteId) {
        return pageRepository.findByIdAndSiteId(pageId, siteId);
    }

    @Override
    public long pageCount(){
        return pageRepository.count();
    }

    @Override
    public long pageCount(long siteId){
        return pageRepository.count(siteId);
    }

    @Override
    public void deletePage(Page page) {
        pageRepository.delete(page);
    }

}

package org.spring;

import org.spring.model.Field;
import org.spring.model.Site;
import org.spring.model.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.spring.services.repo.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
@Component
public class Indexing {

    private final static Log log = LogFactory.getLog(Indexing.class);
    private Setting setting;
    private FieldRepo fieldRepo;
    private SiteRepo siteRepo;
    private IndexRepo indexRepo;
    private PageRepo pageRepo;
    private LemmaRepo lemmaRepo;

    public Indexing(Setting setting, FieldRepo fieldRepo,
                    SiteRepo siteRepo, IndexRepo indexRepo,
                    PageRepo pageRepo, LemmaRepo lemmaRepo) {

        this.setting = setting;
        this.fieldRepo = fieldRepo;
        this.siteRepo = siteRepo;
        this.indexRepo = indexRepo;
        this.pageRepo = pageRepo;
        this.lemmaRepo = lemmaRepo;
    }

    private int threadCount = Runtime.getRuntime().availableProcessors();

    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);

    public boolean allSiteIndexing() throws InterruptedException {
        fieldInit();
        boolean isIndexing;
        List<Site> siteList = getSiteListFromConfig();
        for (Site site : siteList) {
            isIndexing = startSiteIndexing(site);
            if (!isIndexing){
                stopSiteIndexing();
                return false;
            }
        }
        return true;
    }

    public String checkedSiteIndexing(String url) throws InterruptedException {
        List<Site> siteList = siteRepo.getAllSites();
        String baseUrl = "";
        for(Site site : siteList) {
            if(site.getStatus() != Status.INDEXED) {
                return "false";
            }
            if(url.contains(site.getUrl())){
                baseUrl = site.getUrl();
            }
        }
        if(baseUrl.isEmpty()){
            return "not found";
        } else {
            Site site = siteRepo.getSite(baseUrl);
            site.setUrl(url);
            IndexingSite indexing = new IndexingSite(
                    site,
                    setting,
                    fieldRepo,
                    indexRepo,
                    lemmaRepo,
                    pageRepo,
                    siteRepo,
                    false);
            executor.execute(indexing);
            site.setUrl(baseUrl);
            siteRepo.save(site);
            return "true";
        }
    }


    private void fieldInit() {
        Field fieldTitle = new Field("title", "title", 1.0f);
        Field fieldBody = new Field("body", "body", 0.8f);
        if (fieldRepo.getFieldByName("title") == null) {
            fieldRepo.save(fieldTitle);
            fieldRepo.save(fieldBody);
        }
    }

    private boolean startSiteIndexing(Site site) throws InterruptedException {
        Site site1 = siteRepo.getSite(site.getUrl());
        if (site1 == null) {
            siteRepo.save(site);
            IndexingSite indexing = new IndexingSite(
                    siteRepo.getSite(site.getUrl()),
                    setting,
                    fieldRepo,
                    indexRepo,
                    lemmaRepo,
                    pageRepo,
                    siteRepo,
                    true);
            executor.execute(indexing);
            return true;
        } else {
            if (!site1.getStatus().equals(Status.INDEXING)){
                IndexingSite indexing = new IndexingSite(
                        siteRepo.getSite(site.getUrl()),
                        setting,
                        fieldRepo,
                        indexRepo,
                        lemmaRepo,
                        pageRepo,
                        siteRepo,
                        true);
                executor.execute(indexing);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean stopSiteIndexing(){
        boolean isThreadAlive = false;
        if(executor.getActiveCount() == 0){
            return false;
        }

        executor.shutdownNow();
        try {
            isThreadAlive = executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("Ошибка закрытия потоков: " + e);
        }
        if (isThreadAlive){
            List<Site> siteList = siteRepo.getAllSites();
            for(Site site : siteList) {
                site.setStatus(Status.FAILED);
                siteRepo.save(site);
            }
        }
        return isThreadAlive;
    }

    private List<Site> getSiteListFromConfig() {
        List<Site> siteList = new ArrayList<>();
        List<HashMap<String, String>> sites = setting.getSite();
        for (HashMap<String, String> map : sites) {
            String url = "";
            String name = "";
            for (Map.Entry<String, String> siteInfo : map.entrySet()) {
                if (siteInfo.getKey().equals("name")) {
                    name = siteInfo.getValue();
                }
                if (siteInfo.getKey().equals("url")) {
                    url = siteInfo.getValue();
                }
            }
            Site site = new Site();
            site.setUrl(url);
            site.setName(name);
            site.setStatus(Status.FAILED);
            siteList.add(site);
        }
        return siteList;
    }
}

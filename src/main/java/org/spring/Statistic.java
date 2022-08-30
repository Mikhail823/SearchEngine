package org.spring;

import org.spring.model.Site;
import org.spring.model.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.spring.services.entityResponceIndex.Detailed;
import org.spring.services.entityResponceIndex.Statistics;
import org.spring.services.entityResponceIndex.Total;
import org.spring.services.responces.StatisticResponseService;
import org.spring.services.repo.LemmaRepo;
import org.spring.services.repo.PageRepo;
import org.spring.services.repo.SiteRepo;
import org.spring.services.repo.StatisticService;

import java.util.List;

@Service
public class Statistic implements StatisticService {

    private static final Log log = LogFactory.getLog(Statistic.class);

    private final SiteRepo siteRepo;
    private final LemmaRepo lemmaRepo;
    private final PageRepo pageRepo;

    public Statistic(SiteRepo siteRepo, LemmaRepo lemmaRepo, PageRepo pageRepo) {
        this.siteRepo = siteRepo;
        this.lemmaRepo = lemmaRepo;
        this.pageRepo = pageRepo;
    }

    public StatisticResponseService getStatistic(){
        Total total = getTotal();
        List<Site> siteList = siteRepo.getAllSites();
        Detailed[] detaileds = new Detailed[siteList.size()];
        for (int i = 0; i < siteList.size(); i++) {
            detaileds[i] = getDetailed(siteList.get(i));
        }
        log.info("Получение статистики.");
        return new StatisticResponseService(true, new Statistics(total, detaileds));
    }

    private Total getTotal(){
        long sites = siteRepo.siteCount();
        long lemmas = lemmaRepo.lemmaCount();
        long pages = pageRepo.pageCount();
        boolean isIndexing = isSitesIndexing();
        return new Total(sites, pages, lemmas, isIndexing);

    }

    private Detailed getDetailed(Site site){
        String url = site.getUrl();
        String name = site.getName();
        Status status = site.getStatus();
        long statusTime = site.getStatusTime().getTime();
        String error = site.getLastError();
        long pages = pageRepo.pageCount(site.getId());
        long lemmas = lemmaRepo.lemmaCount(site.getId());
        return new Detailed(url, name, status, statusTime, error, pages, lemmas);
    }

    private boolean isSitesIndexing(){
        boolean is = true;
        for(Site site : siteRepo.getAllSites()){
            if(!site.getStatus().equals(Status.INDEXED)){
                is = false;
                break;
            }
        }
    return is;
    }
}

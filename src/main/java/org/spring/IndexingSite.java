package org.spring;

import org.spring.model.*;
import org.spring.morphology.MorphologyAnalyzer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.spring.services.repo.*;
import org.spring.sitemap.BuilderMapSite;

import java.io.IOException;
import java.util.*;

@Component
public class IndexingSite extends Thread{

    private Site site;
    private Setting setting;
    private FieldRepo fieldRepo;
    private IndexRepo indexRepo;
    private LemmaRepo lemmaRepo;
    private PageRepo pageRepo;
    private SiteRepo siteRepo;
    private boolean allSite;

    public IndexingSite(Site site, Setting setting, FieldRepo fieldRepo,
                        IndexRepo indexRepo, LemmaRepo lemmaRepo,
                        PageRepo pageRepo, SiteRepo siteRepo,
                        boolean allSite) {
        this.site = site;
        this.setting = setting;
        this.fieldRepo = fieldRepo;
        this.indexRepo = indexRepo;
        this.lemmaRepo = lemmaRepo;
        this.pageRepo = pageRepo;
        this.siteRepo = siteRepo;
        this.allSite = allSite;
    }
     @Override
    public void run(){
         try {
             if (allSite) {
                 runAllIndexing();
             } else {
                 runOneSiteIndexing(site.getUrl());
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

    public void runAllIndexing() {
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        siteRepo.save(site);
        BuilderMapSite builder = new BuilderMapSite(site.getUrl(), this.isInterrupted());
        builder.builtSiteMap();
        List<String> allSiteUrls = builder.getSiteMap();
        for(String url : allSiteUrls) {
            runOneSiteIndexing(url);
        }
    }

    public void runOneSiteIndexing(String searchUrl) {
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        siteRepo.save(site);
        List<Field> fieldList = getFieldListFromDB();
        try {
            Page page = getSearchPage(searchUrl, site.getUrl(), site.getId());
            Page checkPage = pageRepo.getPage(searchUrl.replaceAll(site.getUrl(), ""));
            if (checkPage != null){
                prepareDbToIndexing(checkPage);
            }
            TreeMap<String, Integer> map = new TreeMap<>();
            TreeMap<String, Float> indexing = new TreeMap<>();
            for (Field field : fieldList){
                String name = field.getName();
                float weight = field.getWeight();
                String stringByTeg = getStringByTag(name, page.getContent());
                MorphologyAnalyzer analyzer = new MorphologyAnalyzer();
                TreeMap<String, Integer> tempMap = analyzer.textAnalyzer(stringByTeg);
                map.putAll(tempMap);
                indexing.putAll(indexingLemmas(tempMap, weight));
            }
            lemmaToDB(map, site.getId());
            map.clear();
            pageSaveToDb(page);
            indexingDb(indexing, page.getPath());
            indexing.clear();
        }
        catch (UnsupportedMimeTypeException e) {
            site.setLastError("Формат страницы не поддерживается: " + searchUrl);
            site.setStatus(Status.FAILED);
        }
        catch (IOException e) {
            site.setLastError("Ошибка чтения страницы: " + searchUrl + "\n" + e.getMessage());
            site.setStatus(Status.FAILED);
        }
        finally {
            siteRepo.save(site);
        }
        site.setStatus(Status.INDEXED);
        siteRepo.save(site);
    }
    private void pageSaveToDb(Page page) {
        pageRepo.save(page);
    }

    private Page getSearchPage(String url, String baseUrl, int siteId) throws IOException {
        Page page = new Page();
        Connection.Response response = Jsoup.connect(url)
                .userAgent(setting.getAgent())
                .referrer("http://www.google.com")
                .execute();

        String content = response.body();
        String path = url.replaceAll(baseUrl, "");
        int code = response.statusCode();
        page.setCode(code);
        page.setPath(path);
        page.setContent(content);
        page.setSiteId(siteId);
        return page;
    }

    private List<Field> getFieldListFromDB() {
        List<Field> list = new ArrayList<>();
        Iterable<Field> iterable = fieldRepo.getAllField();
        iterable.forEach(list::add);
        return list;
    }

    private String getStringByTag (String tag, String html) {
        String string = "";
        Document document = Jsoup.parse(html);
        Elements elements = document.select(tag);
        StringBuilder builder = new StringBuilder();
        elements.forEach(element -> builder.append(element.text()).append(" "));
        if (builder != null){
            string = builder.toString();
        }
        return string;
    }

    private void lemmaToDB (TreeMap<String, Integer> lemmaMap, long siteId) {
        for (Map.Entry<String, Integer> lemma : lemmaMap.entrySet()) {
            String lemmaName = lemma.getKey();
            List<Lemma> lemma1 = lemmaRepo.getLemma(lemmaName);
            Lemma lemma2 = lemma1.stream().
                    filter(lemma3 -> lemma3.getSiteId() == siteId).
                    findFirst().
                    orElse(null);
            if (lemma2 == null){
                Lemma newLemma = new Lemma(lemmaName, 1, siteId);
                lemmaRepo.save(newLemma);
            } else {
                int count = lemma2.getFrequency();
                lemma2.setFrequency(++count);
                lemmaRepo.save(lemma2);
            }
        }
    }

    //Связка леммы и страницы
    private TreeMap<String, Float> indexingLemmas (TreeMap<String, Integer> lemmas, float weight) {
        TreeMap<String, Float> map = new TreeMap<>();
        for (Map.Entry<String, Integer> lemma : lemmas.entrySet()) {
            String name = lemma.getKey();
            float depth;
            if (!map.containsKey(name)) {
                depth = (float) lemma.getValue() * weight;
            } else {
                depth = map.get(name) + ((float) lemma.getValue() * weight);
            }
            map.put(name, depth);
        }
        return map;
    }

    private void indexingDb (TreeMap<String, Float> map, String path){
        Page page = pageRepo.getPage(path);
        long pathId = page.getId();
        long siteId = page.getSiteId();
        for (Map.Entry<String, Float> lemma : map.entrySet()) {
            String lemmaName = lemma.getKey();
            List<Lemma> lemma1 = lemmaRepo.getLemma(lemmaName);
            for (Lemma l : lemma1) {
                if (l.getSiteId() == siteId) {
                    long lemmaId = l.getId();
                    Index indexing = new Index(pathId, lemmaId, lemma.getValue());
                    indexRepo.save(indexing);
                }
            }
        }
    }

    private void prepareDbToIndexing(Page page) {
        List<Index> indexingList = indexRepo.getAllIndexingByPageId(page.getId());
        List<Lemma> allLemmasIdByPage = lemmaRepo.findLemmasByIndexing(indexingList);
        lemmaRepo.deleteAllLemmas(allLemmasIdByPage);
        indexRepo.deleteAllIndexing(indexingList);
        pageRepo.deletePage(page);
    }

}

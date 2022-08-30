package org.spring.sitemap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

public class SiteMap extends RecursiveTask<String> {
    public static Set<String> urlList = new HashSet<>();


    private String url;
    private boolean isInterrupted;

    public SiteMap(){}

    public SiteMap(String url, boolean isInterrupted) {
        this.url = url;
        this.isInterrupted = isInterrupted;
    }

    @Override
    protected String compute() {
        if(isInterrupted){
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(url);
        try {
            Thread.sleep(200);
            Document doc = getDocumentByUrl(url);
            Elements rootElements = doc.getElementsByTag("a");

            List<SiteMap> linkGrabers = new ArrayList<>();
            rootElements.forEach(element -> {
                String link = element.attr("abs:href");
                if (link.startsWith(element.baseUri())
                        && !link.equals(element.baseUri())
                        && !link.contains("#")
                        && !link.contains(".pdf")
                        && !urlList.contains(link)
                ) {
                    urlList.add(link.replaceAll(element.baseUri(), ""));
                    SiteMap linkGraber = new SiteMap(link, false);
                    linkGraber.fork();
                    linkGrabers.add(linkGraber);
                }
            });

            for (SiteMap lg : linkGrabers) {
                String text = lg.join();
                if (!text.equals("")) {
                    result.append("\n");
                    result.append(text);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка парсинга сайта: " + url);
        }
        return result.toString();
    }

    protected Document getDocumentByUrl (String url) throws InterruptedException, IOException {
        Thread.sleep(200);
        return Jsoup.connect(url)
                .maxBodySize(0)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .get();
    }

    public Set<String> getUrlList() {
        return urlList;
    }
}

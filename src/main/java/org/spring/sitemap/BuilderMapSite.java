package org.spring.sitemap;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Component
public class BuilderMapSite {

    private String url;
    private boolean isInterrupted;
    private List<String> siteMap;

    public BuilderMapSite(String url, boolean isInterrupted) {
        this.url = url;
        this.isInterrupted = isInterrupted;
    }

    public void builtSiteMap() {
        String text = new ForkJoinPool().invoke(new SiteMap(url, isInterrupted));
        siteMap = stringToList(text);
    }

    private List<String> stringToList (String text) {
        return Arrays.stream(text.split("\n")).collect(Collectors.toList());
    }

    public List<String> getSiteMap() {
        return siteMap;
    }
}

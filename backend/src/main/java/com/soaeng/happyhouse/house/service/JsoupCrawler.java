package com.soaeng.happyhouse.house.service;

import com.soaeng.happyhouse.house.dao.HouseDao;
import com.soaeng.happyhouse.house.dto.response.NewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsoupCrawler {

    private final HouseDao houseDao;

    private static final String REGION_URL = "https://land.naver.com/news/region.naver";
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int IMAGE_TIMEOUT_MS = 3000;

    @Cacheable(value = "news", key = "#dongCode")
    public List<NewsDto> crawlNews(Long dongCode) {
        Long gugunCode = houseDao.getGugunCode(dongCode);
        String url = REGION_URL + (dongCode == 0 ? "" : "?dvsn_no=" + gugunCode);

        try {
            Document document = Jsoup.connect(url).timeout(CONNECT_TIMEOUT_MS).get();
            return getRegionNews(document);
        } catch (IOException e) {
            log.error("뉴스 크롤링 실패 dongCode={}: {}", dongCode, e.getMessage());
            return List.of();
        }
    }

    private List<NewsDto> getRegionNews(Document document) {
        Elements dl = document.select(".section_headline .headline_list li dl");

        List<String> urls = new ArrayList<>();
        List<NewsDto.NewsDtoBuilder> builders = new ArrayList<>();

        for (Element element : dl) {
            String articleUrl = element.select("dt a[target=_blank]").attr("href");
            String title = element.select("dt a[target=_blank]").text();
            Element dd = element.selectFirst("dd");
            String content = dd.html().split("<span ")[0];
            String publish = dd.select("span.writing").text();
            String date = dd.select("span.date").text();

            urls.add(articleUrl);
            builders.add(NewsDto.builder()
                    .url(articleUrl)
                    .title(title)
                    .content(content)
                    .publish(publish)
                    .date(date));
        }

        // 모든 기사 이미지를 병렬로 fetch
        List<CompletableFuture<String>> imageFutures = urls.stream()
                .map(u -> CompletableFuture.supplyAsync(() -> fetchFirstImage(u)))
                .toList();

        List<String> images = imageFutures.stream()
                .map(CompletableFuture::join)
                .toList();

        List<NewsDto> newsList = new ArrayList<>();
        for (int i = 0; i < builders.size(); i++) {
            newsList.add(builders.get(i).img(images.get(i)).build());
        }

        return newsList;
    }

    private String fetchFirstImage(String newsUrl) {
        try {
            Document doc = Jsoup.connect(newsUrl).timeout(IMAGE_TIMEOUT_MS).get();
            Element article = doc.selectFirst("#newsct_article");
            if (article != null) {
                Element imgTag = article.selectFirst("img");
                if (imgTag != null) return imgTag.attr("data-src");
            }
        } catch (Exception e) {
            log.warn("이미지 fetch 실패 url={}: {}", newsUrl, e.getMessage());
        }
        return null;
    }
}

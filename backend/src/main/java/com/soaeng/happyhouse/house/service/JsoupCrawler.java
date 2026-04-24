package com.soaeng.happyhouse.house.service;

import com.soaeng.happyhouse.house.dao.HouseDao;
import com.soaeng.happyhouse.house.dto.response.NewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsoupCrawler {

    private final HouseDao houseDao;
    private static final String REGION_URL = "https://land.naver.com/news/region.naver";

    public List<NewsDto> crawlNews(Long dongCode) {
        Long gugunCode = houseDao.getGugunCode(dongCode);
        Connection conn = Jsoup.connect(REGION_URL + (dongCode == 0 ? "" : "?dvsn_no=" + gugunCode));

        try {
            Document document = conn.get();
            return getRegionNews(document);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<NewsDto> getRegionNews(Document document) {
        List<NewsDto> newsList = new ArrayList<>();
        Elements dl = document.select(".section_headline .headline_list li dl");

        for (Element element : dl) {
            String url = element.select("dt a[target=_blank]").attr("href");
            String title = element.select("dt a[target=_blank]").text();
            String img = fetchFirstImage(url);
            Element dd = element.selectFirst("dd");
            String content = dd.html();
            content = content.split("<span ")[0];
            String publish = dd.select("span.writing").text();
            String date = dd.select("span.date").text();

            NewsDto dto = NewsDto.builder()
                    .url(url)
                    .title(title)
                    .content(content)
                    .publish(publish)
                    .date(date)
                    .img(img)
                    .build();

            newsList.add(dto);
        }

        return newsList;
    }

    private String fetchFirstImage(String newsUrl) {
        try {
            Document doc = Jsoup.connect(newsUrl).get();
            Element article = doc.selectFirst("#newsct_article");

            if (article != null) {
                Element imgTag = article.selectFirst("img");
                if (imgTag != null) {
                    return imgTag.attr("data-src"); // 첫 번째 이미지 URL 반환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
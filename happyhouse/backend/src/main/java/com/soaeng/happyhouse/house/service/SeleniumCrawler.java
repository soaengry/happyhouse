package com.soaeng.happyhouse.house.service;

import com.soaeng.happyhouse.house.dto.response.NewsDto;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SeleniumCrawler {

    private static final String URL = "https://land.naver.com/news/region.naver";

    public List<NewsDto> process() {
        // 1. 크롬 드라이버 설치 : 현재 PC에 설치된 크롬 브라우저 버전에 맞는 드라이버 자동 다운로드 및 세팅
        WebDriverManager.chromedriver().setup();

        // 2. 크롬 옵션 설정 : --headless 설정 시 브라우저 UI 없이 백그라운드 실행
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");

        // 3. WebDriver 인스턴스 생성 : ChromeDriver는 실제 크롬 브라우저를 제어하는 역할
        WebDriver webDriver = new ChromeDriver(chromeOptions);

        // 4. 페이지 로딩 대기 설정
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        List<NewsDto> newsDtoList;

        try {
            // 5. 지정한 URL로 이동
            webDriver.get(URL);

            // 6. 페이지 로딩 대기 (body 로딩까지 대기)
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            log.info("접속 완료 : {}", URL);

            List<WebElement> dlList = webDriver.findElements(By.cssSelector(".headline_list li dl"));
            newsDtoList = new ArrayList<>();
            for (WebElement dl : dlList) {
                NewsDto dto = createNewsDto(dl);
                newsDtoList.add(dto);
            }

        } catch (Exception e) {
            log.error("크롤링 중 오류 발생 : {}", e.getMessage());
            throw new RuntimeException(e);

        } finally {
            // 8. WebDriver 종료 및 리소스 해제 : 생략 시 메모리 누수 발생 가능
            webDriver.quit();
        }

        return newsDtoList;
    }

    public NewsDto createNewsDto(WebElement dl) {

        // 제목과 URL
        WebElement titleAnchor = dl.findElement(By.cssSelector("dt a"));
        String url = titleAnchor.getDomAttribute("href");
        String title = titleAnchor.getText();

        // 이미지
        String img = "";
        try {
            WebElement imgTag = dl.findElement(By.cssSelector("dt.photo a img"));
            img = imgTag.getDomAttribute("src");
        } catch (Exception e) {
            // 이미지 없을 경우 예외 처리
        }

        // 내용, 언론사, 날짜
        WebElement dd = dl.findElement(By.tagName("dd"));
        String content = dd.getText();
        String publish = dd.findElement(By.cssSelector("span.writing")).getText();
        String date = dd.findElement(By.cssSelector("span.date")).getText();

        return NewsDto.builder()
                .url(url)
                .title(title)
                .content(content)
                .publish(publish)
                .date(date)
                .img(img)
                .build();
    }
}

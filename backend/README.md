# 🔧 Happy House — Backend

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![MyBatis](https://img.shields.io/badge/MyBatis-3.0.5-000000?style=flat-square)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-6379-DC382D?style=flat-square&logo=redis&logoColor=white)

Spring Boot 3.5.6 기반 REST API 서버. 포트 **8080**, 컨텍스트 경로 `/api`.

---

## 📦 기술 스택 및 주요 의존성

| 의존성 | 버전 | 용도 |
|---|---|---|
| Spring Boot | 3.5.6 | 애플리케이션 프레임워크 |
| Spring Security | 6.x | 인증/인가 |
| Spring Data JPA | 3.x | 단순 CRUD ORM |
| MyBatis | 3.0.5 | 복잡한 SQL 쿼리 |
| JJWT | 0.12.6 | JWT 생성/검증 |
| OAuth2 Client | 6.x | Google/Kakao/Naver 소셜 로그인 |
| Spring Data Redis | 3.x | RefreshToken 저장 |
| MySQL Connector | 8.x | DB 드라이버 |
| Selenium | 4.34.0 | 부동산 뉴스 크롤링 |
| jsoup | 1.21.1 | HTML 파싱 |
| Thumbnailator | 0.4.20 | 이미지 리사이징 |
| Lombok | 최신 | 보일러플레이트 코드 제거 |

---

## ⚙️ 환경 설정

### application.yml (공통)

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  profiles:
    active: local
    include: env, oauth       # application-env.yml, application-oauth.yml 포함

  jpa:
    hibernate:
      ddl-auto: update        # 스키마 자동 관리

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 30MB

upload:
  path: upload/               # 파일 업로드 저장 경로

jwt:
  access-expiration: 3600     # AccessToken 유효시간 (초) = 1시간
  refresh-expiration: 604800  # RefreshToken 유효시간 (초) = 7일
```

### application-env.yml (직접 생성 필요 — git 미추적)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/happyhouse_data?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
    username: YOUR_DB_USERNAME
    password: YOUR_DB_PASSWORD
    driver-class-name: com.mysql.cj.jdbc.Driver

  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: YOUR_256BIT_SECRET_KEY

api:
  service-key: YOUR_PUBLIC_DATA_API_KEY   # 공공데이터포털 API 키
```

### application-oauth.yml (직접 생성 필요 — git 미추적)

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
            redirect-uri: http://localhost:8080/api/login/oauth2/code/google
            scope: profile, email

          kakao:
            client-id: YOUR_KAKAO_REST_API_KEY
            client-secret: YOUR_KAKAO_CLIENT_SECRET
            redirect-uri: http://localhost:8080/api/login/oauth2/code/kakao
            authorization-grant-type: authorization_code
            scope: profile_nickname, account_email

          naver:
            client-id: YOUR_NAVER_CLIENT_ID
            client-secret: YOUR_NAVER_CLIENT_SECRET
            redirect-uri: http://localhost:8080/api/login/oauth2/code/naver
            authorization-grant-type: authorization_code
            scope: name, email
```

---

## 🚀 설치 및 실행

### 사전 요구사항

- Java 17+
- MySQL 8.0 (DB: `happyhouse_data`)
- Redis 6.x

### 단계별 실행

```bash
# 1. 프로젝트 루트에서 backend 디렉토리로 이동
cd backend

# 2. 환경설정 파일 생성 (위 섹션 참고)
#    src/main/resources/application-env.yml
#    src/main/resources/application-oauth.yml

# 3. 업로드 디렉토리 생성 (최초 1회)
mkdir -p ../upload/profile ../upload/board

# 4. 빌드 (테스트 제외)
./gradlew build -x test

# 5. 애플리케이션 실행
./gradlew bootRun

# 또는 JAR 직접 실행
java -jar build/libs/happyhouse-*.jar
```

서버 기동 후 `http://localhost:8080/api` 로 접속합니다.

---

## 🗂️ API 엔드포인트

### 인증 (Auth)

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| POST | `/login` | ✗ | 이메일/비밀번호 로그인 |
| GET | `/oauth2/authorization/{provider}` | ✗ | OAuth2 로그인 시작 |
| POST | `/jwt/exchange` | ✓ | OAuth Cookie → Header 토큰 교환 |
| POST | `/jwt/refresh` | ✓ | AccessToken 갱신 (토큰 순환) |
| POST | `/logout` | ✓ | 로그아웃 (RefreshToken 무효화) |

### 사용자 (User) — `/user`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| POST | `/user` | ✗ | 회원가입 |
| POST | `/user/exist` | ✗ | 아이디 중복 확인 |
| GET | `/user` | ✓ | 내 정보 조회 |
| GET | `/user/image?fileName=` | ✗ | 프로필 이미지 다운로드 |
| PUT | `/user` | ✓ | 내 정보 수정 (multipart) |
| DELETE | `/user` | ✓ | 회원 탈퇴 |

### 지역 코드 (Address)

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/sido` | ✗ | 시/도 목록 조회 |
| GET | `/gugun/{sidoCode}` | ✗ | 구/군 목록 조회 |
| GET | `/dong/{gugunCode}` | ✗ | 읍/면/동 목록 조회 |

### 매물 (House) — `/house`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/house` | ✗ | 아파트 목록 조회 (지역 필터) |
| GET | `/house/{aptCode}` | ✗ | 아파트 실거래가 이력 조회 |
| GET | `/house/busStops` | ✗ | 주변 버스정류장 조회 |
| GET | `/house/subwayStations` | ✗ | 주변 지하철역 조회 |
| GET | `/house/population/{dongCode}` | ✗ | 지역 인구 통계 조회 |
| GET | `/house/news?dongCode=` | ✗ | 지역 부동산 뉴스 조회 |

### 북마크 (Bookmark) — `/bookmark`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| POST | `/bookmark/house/{aptCode}` | ✓ | 매물 북마크 추가 |
| DELETE | `/bookmark/house/{aptCode}` | ✓ | 매물 북마크 삭제 |
| GET | `/bookmark/house` | ✓ | 내 매물 북마크 목록 |
| POST | `/bookmark/region/{dongCode}` | ✓ | 지역 북마크 추가 |
| DELETE | `/bookmark/region/{dongCode}` | ✓ | 지역 북마크 삭제 |
| GET | `/bookmark/region` | ✓ | 내 지역 북마크 목록 |

### 게시판 (Board) — `/board`

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/board` | ✗ | 게시글 목록 조회 |
| GET | `/board/{id}` | ✗ | 게시글 상세 조회 |
| POST | `/board` | ✓ | 게시글 작성 (multipart) |
| PUT | `/board/{id}` | ✓ | 게시글 수정 (multipart) |
| DELETE | `/board/{id}` | ✓ | 게시글 삭제 |
| GET | `/board/files/{fileName}` | ✗ | 첨부파일 다운로드 |
| POST | `/board/reply` | ✓ | 댓글 작성 |
| PUT | `/board/reply/{id}` | ✓ | 댓글 수정 |
| DELETE | `/board/reply/{id}` | ✓ | 댓글 삭제 |

---

## 🗄️ 데이터베이스 설정

**RDBMS:** MySQL 8.0  
**DB 이름:** `happyhouse_data`  
**Host:** `localhost:3306`

### 주요 테이블

| 테이블 | 설명 |
|---|---|
| `user` | 사용자 계정 (이메일/OAuth 통합) |
| `jwt_refresh` | RefreshToken 화이트리스트 |
| `sido_code` / `gugun_code` / `dong_code` | 행정구역 계층 코드 |
| `house_data` | 아파트 기본 정보 |
| `house_deal` | 실거래가 이력 |
| `base_address` | 주소 지오코딩 |
| `bookmark_house` / `bookmark_region` | 북마크 |
| `subway_station` | 지하철역 정보 |
| `population` | 지역 인구 통계 |
| `board` / `board_file` / `board_user_read` | 게시판 |
| `reply` | 댓글 |

### MyBatis 매퍼 위치

```
src/main/resources/mapper/
├── house_query.xml   # 매물 검색, 거래 이력, 통계 관련 복잡 쿼리
└── board_query.xml   # 게시판 목록, 검색 관련 쿼리
```

---

## 📂 소스 구조

```
src/main/java/com/soaeng/happyhouse/
├── config/         # SecurityConfig, CorsConfig, JpaAuditingConfig
├── exception/      # CustomException, ErrorCode, ErrorResponse
├── filter/         # JwtAuthenticationFilter, LoginFilter
├── handler/        # SocialLoginSuccessHandler, RefreshTokenLogoutHandler
├── jwt/            # JwtProvider, JwtController + DTOs
├── user/           # UserEntity, UserService, UserController + DTOs/Repository
├── house/          # HouseEntity, HouseService, HouseController + DAOs/DTOs
├── board/          # BoardEntity, BoardService, BoardController + DAOs/DTOs
└── external/       # PublicApiExplorer, NewsCrawler
```

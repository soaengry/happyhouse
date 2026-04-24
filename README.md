# 🏠 Happy House

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Vue](https://img.shields.io/badge/Vue-3.2-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-6379-DC382D?style=flat-square&logo=redis&logoColor=white)
![MyBatis](https://img.shields.io/badge/MyBatis-3.0.5-000000?style=flat-square)
![Pinia](https://img.shields.io/badge/Pinia-3.0-FFD859?style=flat-square&logo=pinia)

> 한국 부동산 아파트 실거래가 검색 SPA (Single Page Application)

---

## 📌 프로젝트 개요

Happy House는 국내 아파트 실거래가 데이터를 지역별 행정구역(시/도 → 구/군 → 읍/면/동) 단위로 검색하고, 관심 매물 북마크, 지역 통계, 부동산 뉴스, 커뮤니티 게시판 기능을 제공하는 풀스택 웹 애플리케이션입니다.

### 주요 기능

- **아파트 검색**: 시/도 → 구/군 → 읍/면/동 계층형 지역 필터링
- **실거래가 조회**: 아파트별 과거 거래 이력 조회
- **북마크**: 관심 매물 및 관심 지역 저장
- **주변 시설 조회**: 주변 버스정류장, 지하철역 정보
- **지역 통계**: 인구 통계 및 지역 기반 정보
- **부동산 뉴스**: 동 코드 기반 최신 부동산 뉴스 크롤링
- **커뮤니티**: 게시판 CRUD, 댓글, 파일 첨부
- **인증**: 이메일/비밀번호 로그인 + Google / Kakao / Naver OAuth2

---

## 🛠 기술 스택

| 구분 | 기술 |
|---|---|
| **Backend** | Java 17, Spring Boot 3.5.6, Spring Security, MyBatis 3.0.5, JPA |
| **Auth** | JWT (JJWT 0.12.6), OAuth2 (Google / Kakao / Naver), Redis |
| **Database** | MySQL 8.0 (`happyhouse_data`), Redis 6.x |
| **Crawling** | Selenium 4.34, jsoup 1.21.1 |
| **Frontend** | Vue 3.2, Vue Router 4.5, Pinia 3.0, Axios 1.12 |
| **UI** | Chart.js 4.5, vue-chartjs 5.3, CKEditor 5, Font Awesome 7 |
| **Build** | Gradle 8.x, Vue CLI 5.0 |

---

## 📁 디렉토리 구조

```
happyhouse/
├── backend/                     # Spring Boot REST API (port 8080)
│   ├── src/
│   │   └── main/
│   │       ├── java/com/soaeng/happyhouse/
│   │       │   ├── config/      # Security, CORS, JPA 설정
│   │       │   ├── jwt/         # JWT 토큰 생성/검증/갱신
│   │       │   ├── filter/      # JwtFilter, LoginFilter
│   │       │   ├── handler/     # OAuth 성공/로그아웃 핸들러
│   │       │   ├── user/        # 사용자 도메인
│   │       │   ├── house/       # 매물·거래·북마크·뉴스 도메인
│   │       │   ├── board/       # 커뮤니티 게시판 도메인
│   │       │   └── external/    # 공공 API 연동
│   │       └── resources/
│   │           ├── application.yml
│   │           └── mapper/      # MyBatis XML 매퍼
│   └── build.gradle
│
├── frontend/                    # Vue 3 SPA (port 3000)
│   ├── src/
│   │   ├── views/               # 페이지 컴포넌트 (8개)
│   │   ├── components/          # 공통 컴포넌트 (5개)
│   │   ├── layouts/             # 레이아웃 컴포넌트
│   │   ├── router/              # Vue Router 설정
│   │   ├── stores/              # Pinia 상태 관리 (5개)
│   │   └── services/            # API 서비스 레이어 (6개)
│   └── package.json
│
├── upload/                      # 파일 업로드 저장 디렉토리
├── .claude/                     # 프로젝트 규칙 & 아키텍처 문서
└── CLAUDE.md
```

---

## ⚡ Quick Start

### 사전 요구사항

- Java 17+
- Node.js 16+ / npm 8+
- MySQL 8.0 (`localhost:3306/happyhouse_data`)
- Redis (`localhost:6379`)

### 1. 저장소 클론

```bash
git clone <repository-url>
cd happyhouse
```

### 2. 업로드 디렉토리 생성

```bash
mkdir -p upload/profile upload/board
```

### 3. 백엔드 환경 설정

`backend/src/main/resources/` 에 아래 두 파일을 생성합니다 (git 미추적):

```yaml
# application-env.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/happyhouse_data?useSSL=false&allowPublicKeyRetrieval=true
    username: YOUR_DB_USER
    password: YOUR_DB_PASSWORD
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: YOUR_JWT_SECRET_KEY          # 최소 256-bit
  access-expiration: 3600
  refresh-expiration: 604800

api:
  service-key: YOUR_PUBLIC_DATA_API_KEY
```

```yaml
# application-oauth.yml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
          kakao:
            client-id: YOUR_KAKAO_REST_API_KEY
            client-secret: YOUR_KAKAO_CLIENT_SECRET
          naver:
            client-id: YOUR_NAVER_CLIENT_ID
            client-secret: YOUR_NAVER_CLIENT_SECRET
```

### 4. 백엔드 실행

```bash
cd backend
./gradlew bootRun
# http://localhost:8080
```

### 5. 프론트엔드 실행

```bash
cd frontend
npm install
npm run serve
# http://localhost:3000
```

---

## 📖 상세 문서

- [Backend README](./backend/README.md) — API 엔드포인트, DB 설정, 빌드 방법
- [Frontend README](./frontend/README.md) — 라우팅 구조, 컴포넌트, 상태 관리

---

## 🔐 인증 흐름 요약

```
OAuth 버튼 클릭
  → /api/oauth2/authorization/{provider}
  → 소셜 로그인 완료
  → SocialSuccessHandler: RefreshToken → HTTP-only Cookie
  → /cookie 페이지 리디렉션
  → POST /api/jwt/exchange: Cookie → localStorage (AccessToken + RefreshToken)
  → 이후 요청: Authorization: Bearer {accessToken}
  → 401 발생 시: /api/jwt/refresh → 토큰 갱신 후 재시도
```

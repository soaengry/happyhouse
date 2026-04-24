# 🖥️ Happy House — Frontend

![Vue](https://img.shields.io/badge/Vue-3.2-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white)
![Vue Router](https://img.shields.io/badge/Vue_Router-4.5-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white)
![Pinia](https://img.shields.io/badge/Pinia-3.0-FFD859?style=flat-square)
![Axios](https://img.shields.io/badge/Axios-1.12-5A29E4?style=flat-square)
![Chart.js](https://img.shields.io/badge/Chart.js-4.5-FF6384?style=flat-square&logo=chartdotjs&logoColor=white)

Vue 3 기반 SPA 프론트엔드. 포트 **3000**, 개발 서버는 `/api` 요청을 `http://localhost:8080` 으로 프록시합니다.

---

## 📦 기술 스택 및 주요 의존성

| 의존성 | 버전 | 용도 |
|---|---|---|
| Vue | 3.2.13 | UI 프레임워크 |
| Vue Router | 4.5.1 | 클라이언트 사이드 라우팅 |
| Pinia | 3.0.3 | 전역 상태 관리 |
| Axios | 1.12.2 | HTTP 클라이언트 |
| Chart.js | 4.5.1 | 데이터 시각화 |
| vue-chartjs | 5.3.3 | Chart.js Vue 래퍼 |
| CKEditor 5 | 최신 | 리치 텍스트 에디터 (게시판) |
| Font Awesome | 7.1.0 | 아이콘 |
| vue-toast-notification | 3.1.3 | 토스트 알림 |

---

## ⚙️ 환경 설정

### .env.local (직접 생성 — git 미추적)

```env
VUE_APP_BASE_URL=http://localhost:8080
```

| 변수명 | 기본값 | 설명 |
|---|---|---|
| `VUE_APP_BASE_URL` | `http://localhost:8080` | 백엔드 API 서버 주소 |

### 개발 서버 프록시 (vue.config.js)

```javascript
devServer: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

개발 중 브라우저의 CORS 제약 없이 `/api/*` 요청이 백엔드로 자동 전달됩니다.

---

## 🚀 설치 및 실행

### 사전 요구사항

- Node.js 16+
- npm 8+
- 백엔드 서버 실행 중 (`http://localhost:8080`)

### 단계별 실행

```bash
# 1. frontend 디렉토리로 이동
cd frontend

# 2. 의존성 설치
npm install

# 3. .env.local 생성 (필요 시)
echo "VUE_APP_BASE_URL=http://localhost:8080" > .env.local

# 4. 개발 서버 실행
npm run serve
# → http://localhost:3000

# 5. 프로덕션 빌드
npm run build
# → dist/ 디렉토리에 빌드 결과물 생성

# 6. 코드 린트
npm run lint
```

---

## 🗺️ 주요 페이지 및 라우팅 구조

| 경로 | 뷰 파일 | 인증 필요 | 설명 |
|---|---|---|---|
| `/` | `HouseMain.vue` | ✗ | 아파트 검색 메인 |
| `/login` | `LoginView.vue` | ✗ | 로그인 (이메일 + OAuth) |
| `/join` | `JoinView.vue` | ✗ | 회원가입 |
| `/user` | `UserInfoView.vue` | ✓ | 내 정보 관리 |
| `/bookmark/house` | `HouseMain.vue` | ✓ | 북마크 매물 목록 |
| `/bookmark/region` | `BookmarkRegion.vue` | ✓ | 북마크 지역 목록 |
| `/news` | `NewsView.vue` | ✗ | 부동산 뉴스 피드 |
| `/cookie` | `CookieView.vue` | ✗ | OAuth 토큰 교환 (내부 용도) |
| `/community` | `CommunityListView.vue` | ✗ | 커뮤니티 게시글 목록 |
| `/community/:id` | `CommunityDetailView.vue` | ✗ | 게시글 상세 + 댓글 |
| `/community/write` | `CommunityEditorView.vue` | ✓ | 게시글 작성 |
| `/community/:id/edit` | `CommunityUpdateView.vue` | ✓ | 게시글 수정 |

### 인증 가드 (Navigation Guard)

```javascript
// 인증이 필요한 라우트에서 비로그인 시 /login 으로 리디렉션
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !isAuthenticated()) {
    next('/login')
  } else {
    next()
  }
})
```

---

## 🧩 컴포넌트 구조

```
src/
├── App.vue                      # 루트 컴포넌트
├── main.js                      # 앱 진입점 (Vue, Router, Pinia 등록)
│
├── layouts/
│   └── MainLayout.vue           # 공통 레이아웃 (헤더 + 콘텐츠 + 푸터)
│
├── views/
│   ├── HouseMain.vue            # 매물 검색 메인 페이지
│   ├── LoginView.vue            # 로그인 페이지
│   ├── JoinView.vue             # 회원가입 페이지
│   ├── UserInfoView.vue         # 내 정보 페이지
│   ├── BookmarkRegion.vue       # 북마크 지역 페이지
│   ├── NewsView.vue             # 뉴스 페이지
│   ├── CookieView.vue           # OAuth 토큰 교환 페이지
│   ├── CommunityListView.vue    # 게시판 목록 페이지
│   ├── CommunityDetailView.vue  # 게시글 상세 페이지
│   ├── CommunityEditorView.vue  # 게시글 작성 페이지
│   └── CommunityUpdateView.vue  # 게시글 수정 페이지
│
├── components/
│   ├── HouseCard.vue            # 아파트 카드 (이미지, 가격, 주소)
│   ├── HouseDetailModal.vue     # 상세 모달 (거래이력, 주변시설, 통계)
│   ├── TheHeader.vue            # 상단 네비게이션 바
│   └── ThePagination.vue        # 페이지네이션 컨트롤
│
├── router/
│   └── index.js                 # 라우트 정의 + 인증 가드
│
├── stores/
│   ├── userStore.js             # 사용자 인증/프로필 상태
│   ├── houseStore.js            # 매물 검색/북마크/주변시설 상태
│   ├── addressStore.js          # 지역 코드 계층 상태
│   ├── boardStore.js            # 게시판 상태
│   └── paginationStore.js       # 페이지네이션 상태
│
├── services/
│   ├── index.js                 # Axios 인스턴스 + 인터셉터
│   ├── userService.js           # 사용자 API
│   ├── houseService.js          # 매물/북마크 API
│   ├── addressService.js        # 지역 코드 API
│   ├── boardService.js          # 게시판 API
│   └── apiHelper.js             # 공통 API 유틸
│
└── utils/
    ├── constants.js             # 상수 (TOKEN_KEYS, API 경로 등)
    └── date.js                  # 날짜 포맷 유틸
```

---

## 🔄 Axios 인터셉터 동작

```
요청 시:
  → Request Interceptor
  → localStorage에서 AccessToken 읽어 헤더에 추가
  → Authorization: Bearer {accessToken}

응답 시:
  → 정상 응답: 그대로 반환
  → 401 Unauthorized:
      → POST /api/jwt/refresh (RefreshToken 전송)
      → 새 AccessToken 발급 및 localStorage 저장
      → 원래 요청 재시도
      → 재시도도 실패: 로그아웃 처리 후 /login 이동
```

---

## 🗃️ 상태 관리 (Pinia Stores)

### userStore

```javascript
// 주요 상태
user, isLoading, error

// 주요 액션
register(userData)        // 회원가입
checkUsername(username)   // 아이디 중복 확인
fetchUserInfo()           // 내 정보 조회
updateUserInfo(formData)  // 내 정보 수정 (multipart)
logout()                  // 로그아웃
```

### houseStore

```javascript
// 주요 상태
houseList, houseCount, bookmarks
busStopList, subwayStationList, population, newsList

// 주요 액션
getHouseList(params)           // 매물 목록 조회
getDealList(aptCode)           // 실거래가 이력 조회
toggleBookmark(aptCode)        // 북마크 토글
getBusStopList(lat, lng)       // 주변 버스정류장
getSubwayStationList(lat, lng) // 주변 지하철역
getPopulation(dongCode)        // 지역 인구 통계
getNews(dongCode)              // 부동산 뉴스
```

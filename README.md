## 📝 Technology Stack
| Category             | Technology                                                                 |
|----------------------|---------------------------------------------------------------------------|
| **Language**         | Java 17                                                                  |
| **Framework**        | Spring Boot 3.3.5                                                        |
| **Databases**        | MySQL, Redis                                                             |
| **Authentication**   | JWT, Spring Security, OAuth2.0                                           |
| **Development Tools**| Lombok, Querydsl                                                         |
| **API Documentation**| Swagger UI (SpringDoc)                                                   |
| **Storage**          | AWS S3                                                                   |
| **Monitoring**       | Spring Boot Actuator, Prometheus, , Logback,                              |
| **NLP**              | KOMORAN (Korean Morphological Analyzer)                                  |
| **Build Tools**      | Gradle    |

## 🔑 Key Features
### 1. 게시판
- 팬들이 선호하는 키워드로 글이 분류된 게시판
- 정보/후기/배우 등의 상위 게시판과 게시판 내 하위 카테고리로 글을 분류
### 2. 티켓북
- 공연 관람 내역을 추가하고 관리하는 티켓북
- 등록된 공연 정보로 보다 손쉽게 공연 관람 내역 추가
### 3. 프리미엄 리뷰
- 공연에 특화된 세부 기준으로 후기를 남기는 프리미엄 리뷰
- 공연의 만족도를 결정하는 기준이 담긴 가이드를 제공하며 통일된 후기 양식
### 4. 뮤지컬 검색
### 5. 마이페이지
### 6. 소셜 회원가입/로그인


## 🔨 Project Architecture
![image](https://github.com/user-attachments/assets/0c46be51-f395-4ecd-85e7-e205ab73415f)
<br></br>
#### 1. Spring Boot 기반의 모놀리틱 아키텍처
- **Spring Boot**를 중심으로 애플리케이션이 설계되었으며, 모놀리틱 구조로 구현.  
- Docker를 이용해 컨테이너화하여 배포와 관리를 용이하게 함.
- **RDS -MySQL**를 데이터베이스로 사용하여 안정적인 데이터 저장을 보장.

---

#### 2. Redis를 이용한 캐싱
- **Redis**를 캐싱 레이어로 활용하여 데이터 접근 속도를 개선하고 서버 부하를 줄임.

---

#### 3. GitHub Actions를 이용한 CI/CD 파이프라인
- GitHub Actions를 사용하여 **CI/CD 파이프라인**을 구축.
  - `application.yml` 생성
  - Java Gradle 빌드  
  - Docker 이미지를 빌드하고 **Docker Hub**에 Push  
- PR(Pull Request) 또는 Push 이벤트 발생 시 자동화된 배포 및 Slack 알림 기능을 통해 개발 효율성을 높임.  

---

#### 4. Actuator, Prometheus, Grafana를 이용한 서버 모니터링
- Spring Boot의 **Actuator**를 통해 애플리케이션 상태를 노출.  
- **Prometheus**가 Actuator에서 데이터를 수집하고, 이를 **Grafana**를 통해 시각화하여 서버 상태를 실시간으로 모니터링.
- 서버의 성능, 가용성, 메모리 사용량 등을 직관적으로 확인할 수 있음.
<details>
<summary>모니터링 이미지</summary>
![image](https://github.com/user-attachments/assets/12a3709d-29d0-4a50-9158-48c28b8d23ef)
</details>

---

#### 5. Logback, Promtail, Loki, Grafana를 이용한 서버 로그 모니터링
- 서버의 **Logback** 로그를 Promtail을 통해 수집.  
- **Loki**가 로그를 저장하고, Grafana를 사용해 로그를 검색 및 시각화.  
- 이를 통해 애플리케이션에서 발생하는 오류를 추적하고 문제를 신속히 해결.
<details>
<summary>모니터링 이미지</summary>
![image](https://github.com/user-attachments/assets/cef65857-2e05-458d-92c7-55652d42d804)
</details>

---

#### 6. AWS S3를 이용한 이미지 저장
- Presigned URL을 통해 사용자가 안전하게 이미지를 S3에 업로드.  
- 프론트엔드에서 이미지를 바로 업로드하고 필요 시 다운로드할 수 있는 구조로 설계. 

 

## Issue, PR 을 통한 코드리뷰
## 🔨 Package Architecture
이 프로젝트는 **레이어드 아키텍처**를 채택했습니다. 또한 도메인별로 패키지를 분리하여 각 도메인에 맞는 비즈니스 로직과 관련 요소들을 
그룹화하고, 유지보수성과 확정성을 높이는 구조를 채택했습니다.
```
/main/java/encore/server/
│
├── domain/
│   ├── controller/ # API 엔드포인트 처리 클래스의 모음
│   ├── converter/ # Entity -> DTO, DTO -> Entity 로 변환을 수행하는 클래스의 모음
│   ├── dto/ # 응답, 요청 데이터 클래스의 모음
│   ├── entity/ # 엔티티 클래스의 모음
│   ├── repository/ # 데이터베이스 연동 클래스의 모음
│   └── service/ # 비즈니스 로직 처리 클래스의 모음
│
├── global/
│   ├── auth/ # 인증 관련 클래스의 모음
│   ├── common/ # 애플리케이션 전역적으로 사용하는 데이터 클래스
│   ├── config/ # 애플리케이션 설정 클래스
│   ├── exception/ # 예외 처리 클래스
│   ├── filter/ # 필터 클래스
│   └── util/ # 애플리케이션 전역적으로 사용하는 유틸리티 클래스
│
└── EncoreServerApplication.class # 애플리케이션 진입점
```
## 📂 API Document
프로젝트의 API 명세는 아래 링크에서 확인하실 수 있습니다.

## 📂 ERD
![image](https://github.com/user-attachments/assets/823a3fe1-f173-4331-8cf1-89726e2dcb61)

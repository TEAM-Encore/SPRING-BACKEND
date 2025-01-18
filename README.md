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
- 정보/후기/배우/자유 게시판 등의 상위 분류와 카테고리 기반 하위 분류 관리를 고려하여 CRUD 개발
- Querydsl을 이용하여 게시판 종류/카테고리/해시태그/정렬/검색어에 따른 커서 기반 페이지네이션 기능 개발
- redis, komoran을 이용한 검색어 자동완성 기능 개발
- 이외에도 게시글 좋아요, 댓글, 댓글 좋아요 기능 개발
### 2. 티켓북
- 공연 관람 내역을 추가하고 관리하는 티켓북 CRUD 개발
- 이외에도 배우 검색 기능 개발
### 3. 프리미엄 리뷰
- 공연에 특화된 세부 기준으로 후기를 남기는 프리미엄 리뷰 CRUD 개발
- 포인트 사용에 따라 리뷰를 조회할 수 있도록 잠금기능 설정
- Querydsl을 이용하여 해시태그/검색어에 따른 커서 기반 페이지네이션 기능 개발
- redis을 이용한 최근 검색어 조회 및 삭제 기능 개발
- redis와 komoran을 이용한 검색어 자동완성 기능 개발
- 이외에도 리뷰 작성 시 필요한 시야 평가 척도를 위한 이미지 조회, 리뷰 좋아요, 리뷰 신고, 인기 리뷰 리스트 조회 기능 개발
### 4. 뮤지컬
### 5. 마이페이지
### 6. 소셜 회원가입/로그인
- Spring Security 와 OAuth 2.0, Google, Kakao, Apple 의 로그인 API를 이용한 사용자 인증 기능 개발
- JWT 토큰 방식의 사용자 인증/인가 기능 개발


## 🔨 Project Architecture
![image](https://github.com/user-attachments/assets/0c46be51-f395-4ecd-85e7-e205ab73415f)
<br></br>
#### 1. Spring Boot 기반의 모놀리틱 아키텍처
- **Spring Boot**를 중심으로 애플리케이션이 설계되었으며, 모놀리틱 구조로 구현.  
- Docker를 이용해 컨테이너화하여 배포와 관리를 용이하게 함.
- **RDS -MySQL**를 데이터베이스로 사용하여 안정적인 데이터 저장을 보장.
-------------------------------------------------

#### 2. Redis를 이용한 캐싱
- **Redis**를 활용하여 데이터 접근 속도를 개선하고 서버 부하를 줄임.
-------------------------------------------------

#### 3. GitHub Actions를 이용한 CI/CD 파이프라인
- GitHub Actions를 사용하여 **CI/CD 파이프라인**을 구축.
- PR 시 ci.yml 을 통해 Build 를 진행함
- main 브랜치로 Push 시 cd.yml 을 통해 도커 이미지를 생성하고 프로덕션 환경에 배포를 진행함.
- PR(Pull Request) 또는 Push 이벤트 발생 시 자동화된 배포 및 Slack 알림 기능을 통해 개발 효율성을 높임.  
-------------------------------------------------

#### 4. Actuator, Prometheus, Grafana를 이용한 서버 모니터링
- Spring Boot의 **Actuator**를 통해 애플리케이션 상태를 노출.  
- **Prometheus**가 Actuator에서 데이터를 수집하고, 이를 **Grafana**를 통해 시각화하여 서버 상태를 실시간으로 모니터링.
- 서버의 성능, 가용성, 메모리 사용량 등을 직관적으로 확인할 수 있음.
<details>
<summary>모니터링 이미지</summary>
  
![image](https://github.com/user-attachments/assets/12a3709d-29d0-4a50-9158-48c28b8d23ef)
</details>

--------------------------------------------------------------------------------------------------


#### 5. Logback, Promtail, Loki, Grafana를 이용한 서버 로그 모니터링
- 서버의 **Logback** 로그를 Promtail을 통해 수집.  
- **Loki**가 로그를 저장하고, Grafana를 사용해 로그를 검색 및 시각화.  
<details>
<summary>모니터링 이미지</summary>
  
![image](https://github.com/user-attachments/assets/cef65857-2e05-458d-92c7-55652d42d804)
</details>

-------------------------------------------------

#### 6. AWS S3를 이용한 이미지 저장
- Presigned URL을 통해 사용자가 안전하게 이미지를 S3에 업로드.  
- 프론트엔드에서 이미지를 바로 업로드하고 필요 시 다운로드할 수 있는 구조로 설계. 

 -------------------------------------------------

## 🔨 Package Architecture
 앙코르는 **3-Layered Architecture**를 채택했습니다. 또한 도메인별로 패키지를 분리하여 각 도메인에 맞는 비즈니스 로직과 관련 요소들을 그룹화하고, 유지보수성과 확정성을 높이는 구조를 채택했습니다.
- **간단한 구조**: 세 가지 주요 계층만으로 구성되어 직관적이고 이해하기 쉬움. 초기 설계 및 구현이 빠름
- **분리된 관심사/유지보수 용이**: 각 계층이 독립적인 역할을 수행하므로 다른 계층에 영향을 최소화함
- **확장성**: 새로운 기능을 추가할 때 각 계층을 독립적으로 확장 가능
![image](https://github.com/user-attachments/assets/272f61e8-0a02-4db0-b444-8a8b502ef2b6)

```
/main/java/encore/server/
│
├── domain/{domain-name}/
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
- API는 기본적으로 RESTful 원칙에 따라 설계되었음
- 버전 관리를 위해 prefix를 사용
- 에러 코드를 세분화하여 관리
<details>
<summary>에러 코드 Enum</summary>
  
  ![image](https://github.com/user-attachments/assets/f62f6194-e03e-4777-956b-f8f4e017bec3)
</details>

- 기본 응답 구조와 에러 응답 구조를 정의하여 응답 일관성을 유지

프로젝트의 API 명세는 아래 링크에서 확인하실 수 있습니다. <br>
![API 명세서 노션 페이지](https://www.notion.so/its-time/API-fffc706af83d81c3b3aee801b61ba002?pvs=4)

## 📅 ERD
![image](https://github.com/user-attachments/assets/3458ce74-af3f-4b5e-8bf6-121499a8b46b)
- User:
  - 이용약관(TermOfUse) 테이블을 따로 두어 버전관리와 약관 변경에 용의하도록 설계
  - 선호키워드(PrefferredKeyword) 테이블을 따로 두어 이후 확장성(최대 선택 키워드 갯수 증가 혹은 내용 변경 등)에 용의하도록 설계.
- Post:
  - 해시태그 테이블을 분리하여 게시글당 해시태그의 관리를 용의하게 설계하였으며, 해시태그를 통한 게시글 검색에 용의하도록 설계.
- Musical:
  - 배우(Actor) 테이블을 이용하여 전체 배우를 관리하도록 설계
  - 뮤지컬 배우(MusicalActor) 테이블을 따로 두어 배우 관리에 용이하도록 설계.
- Ticket:
  - 티켓북 배우(TicketActors) 테이블을 따로 두어 회차마다 배우가 다른 특징을 구현할 수 있도록 설계.
  - Musical id를 이용하여 해당 뮤지컬에 대한 티켓북 생성에 용이하도록 설계.
- Review:
  - 사용자 리뷰(UserReview) 테이블을 따로 두어서 포인트를 사용하여 열람한 리뷰를 관리할 수 있도록 설계.

## 💁 GitHub Issue, Pull Request, Slack을 통한 협업 이용
#### Issue
- 기능 구현, 버그 수정, 개선 사항 등 다양한 작업을 Issue로 등록하여 체계적으로 관리함
- 각 Issue는 세부적으로 할 일을 나누어 다루며, 필요한 경우 우선순위나 진행 상황을 기록하여 팀원들이 진행 상황을 쉽게 파악할 수 있도록 함
  
#### Pull Request
- 기능 구현이 완료된 후, 변경 사항을 PR로 제출
- PR은 코드 리뷰를 위해 사용되며, 팀원들끼리 주어진 코드에 대해 리뷰를 진행하고 개선점을 제시함
- PR을 통해 리뷰 받은 후 최종적으로 main 브랜치에 머지함
- PR에서 코드 리뷰는 필수이며, 이 과정에서 발생한 논의 사항이나 개선 사항을 반영해 코드 품질을 유지함

### Slack
- 기획, 디자인, 프론트엔드 개발자들과 원활한 소통을 위해 Slack을 이용함
- 이슈 생성, PR, merge에 대해 알람 설정하여 실시간으로 진행상황을 공유할 수 있게 함

## 🙌 Git Commit Convention
![깃 커밋 컨벤션](https://www.notion.so/its-time/IT-s-TIME-6-e08ab52953fb4c3e89c8aa36074a8da2?p=cca825cd6bc444c989f3773610b3c2c8&pm=s)

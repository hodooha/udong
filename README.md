<div align="center">

###### [25회차] 멀티잇 백엔드 개발(Java) - 파이널 프로젝트 1팀(너와 난 ON CASCADE) <br><br><br>

</div>
<div align="center">
      
![logo1](https://github.com/user-attachments/assets/f6f2e852-f328-4b1d-965f-7543eece5f64)

</div>

<div align="center"><strong>우동행은 위치 기반 지역 커뮤니티 플랫폼 서비스입니다.</strong></div>

<br>

## &#128161; Overview<br>

![udh-index-page](https://github.com/user-attachments/assets/24d733ef-bb6c-4ceb-93f7-4633caa527a2)

<div align="right"><strong>Design by 팀장 강성현</strong></div>

<br>**📆 프로젝트 기간 : 2024.07.11 ~ 2024.08.26**
<br><br>
## :page_with_curl: 업무분배

|**이름**|**기능**|
|:---:|:---:|
|**[강성현](https://github.com/seonghyoi)**|우동 모임, 우동 소식, 채팅, 메인 디자인 담당|
|**[김재식](https://github.com/agak4)**|소셜 로그인 API, 회원가입, 마이페이지, 알림, 쪽지, 고객센터 담당|
|**[윤정해](https://github.com/yoonjunghae)**|카카오 지도 API, 땡처리, 관리자 페이지 담당|
|**[하지은](https://github.com/hodooha)**|추천 알고리즘, 나눔 당첨자 래플, 대여/나눔 담당|

<br><br>
## 💻 개발환경
### 개발
**언어 : Java version 17**

**Frame work : Spring Boot**

**IDE : IntelliJ**

### DB
**DB : MySQL**

**DMT : DBeaver**

**NAVER CLOUD FLATFORM: Cloud DB for MySQL**

[![stackticon](https://firebasestorage.googleapis.com/v0/b/stackticon-81399.appspot.com/o/images%2F1724372982469?alt=media&token=1d7b420d-0ecf-4d5b-b32e-b895214c15f4)](https://github.com/msdio/stackticon) <br><br>

## &#127912; ERD
![ERD_최종](https://github.com/user-attachments/assets/baaa13a9-3103-4802-8f3e-8f6bf59e5207)
<br><br>

## &#128187; 구현 결과
- [X] **강성현 : 우동 모임, 우동 소식 담당**

- [x] **김재식 : 회원가입, 마이페이지, 고객센터 담당**

- [x] **윤정해 : 땡처리, 관리자페이지 담당**<br>
• 땡처리 : 한 페이지에 게시글 4개 출력<br>
• 낮은가격순, 마감임박순, 최신순으로 정렬<br>
• 제목과 내용의 키워드로 검색<br>
![스크린샷 2024-08-22 211627](https://github.com/user-attachments/assets/5fbef35f-8fc3-40b6-980b-3689ce0ac197)
<br><br>

- [x] **하지은 : 대여/나눔 담당**<br>
• 대여/나눔 : 페이지당 물건 8개 출력<br>
• 추천 물건 목록: 추천 물건 캐러셀 출력(아이템 기반 협업 필터링 알고리즘 사용)<br>
• 카테고리, 상태 필터링 및 키워드 검색<br>
<img src="https://github.com/user-attachments/assets/a82bbb36-aee5-4e88-b9af-cc080b9fe70f" style="width: 100%">
<br><br>

## &#128204; 우리팀 깃 작업 규칙
**메인 : main**<br>
**브랜치 : member/ share/ sale/ club/ news**<br> 

**커밋 메세지 작성법**<br> 
제목은 태그와 이모지, 작업자 이름과 커밋 번호, 요약을 작성<br> 
내용은 작업에 대한 상세 설명을 작성<br> 
제목과 내용 사이에 Enter 공백<br> 
ex) 🧩feat :: (성현 #1) 채팅 기능 추가<br> 
      (공백)<br> 
      모임회원들끼리 채팅 기능 추가<br> 

**이모지 태그 설명**<br>
🗂️ project :: 프로젝트를 세팅한다.<br> 
⚙️ build :: 시스템 또는 외부 종속 파일에 영향을 미치는 설정을 변경<br> 
📑 docs :: 프로젝트 관련 문서 등을 추가/수정 (README.md 등)<br> 
🧩 feat :: 새로운 기능 추가<br> 
💦 chore :: build 관련, 패키지 설정 등 자잘한 작업 수행<br> 
🛠️ fix :: 기존 프로젝트의 버그 수정<br> 
❌ delete :: 파일 등을 삭제<br> 
🔙 revert :: 커밋을 롤백<br> 
🔗 merge :: 브랜치를 main 브랜치에 병합<br> 


<br><br>

## &#128546;  힘들었던 점
![locationTable](https://github.com/user-attachments/assets/9468f318-a4fe-4cd8-8250-6def99dc5641)
<br><br>
저희 프로젝트는 위치 기반 지역 커뮤니티 플랫폼을 개발하는 것이었고, 여기서 핵심은 사용자 위치 정보를 활용해 지역 커뮤니티를 효과적으로 연결하는 것이었습니다. 이 플랫폼에서 가장 중요한 부분은 위치 정보 데이터를 정확하고 효율적으로 처리하는 것이었습니다.

#### 데이터 활용 및 처리 과정<br>
저희는 국토교통부에서 제공하는 전국 법정동 오픈 API 데이터를 활용했습니다. 이 데이터에는 전국의 법정동 정보가 담겨 있었고, 총 49,874개의 행으로 구성되어 있었습니다. 이렇게 고 멋지게 프로젝트를 마칠 수 있었습니다. 
            이슈나 에러에 대해 같이 토론하고 해결해가는 과정 속에서 협업의 의미와 가치를 다시 한번 느꼈습니다. 
            모두들 정말 고생 많으셨고 감사합니다!😍 </pre> 



<br><br>
      

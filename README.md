# 프로젝트 내용

  ## Board
  **자유롭게 글을 작성할 수 있는 게시판**

  ------
  ### 기술 스택 
  #### 프로그래밍 언어
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"></a>  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"></a>
  #### 프레임워크 및 라이브러리
  <img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"></a>  <img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white"></a>
  #### 서버
  <img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"></a>  <img src="https://img.shields.io/badge/apache tomcat-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=white"></a>
  #### 데이터 베이스
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"></a>  <img src="https://img.shields.io/badge/amazon rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"></a> 
  #### 개발 환경
  <img src="https://img.shields.io/badge/intellij idea-000000?style=for-the-badge&logo=intellijidea&logoColor=white"></a>  <img src="https://img.shields.io/badge/linux-FCC624?style=for-the-badge&logo=linux&logoColor=white"></a>
  
  -----
  ### 사용방법
  #### <p>1. <a href ="http://ec2-15-164-58-170.ap-northeast-2.compute.amazonaws.com:8080">board</a> 왼쪽 링크를 눌러 사이트에 접속한다.</p>
  #### <p>2. 로그인 버튼을 눌러 Id : guest , Password : guest로 로그인한다.</p>
  #### <p>3. 로그인 한 계정으로 글 작성을 한다.</p>
  
  -----
  ### 구현 기능
    1. 글 작성 및 댓글 작성 구현
    2. 로그인 및 개인 정보 변경 기능 구현
    3. 자신이 작성한 글 및 댓글 검색 가능
    4. 키워드로 검색 가능
  -----
  ### 화면 구성
  
  |메인 페이지|댓글 페이지|
  |---|---|
  |![메인 페이지](https://github.com/user-attachments/assets/6b85c915-b617-4a64-a6d2-1306f555feae)| ![답변 달기](https://github.com/user-attachments/assets/11e7e970-c848-4d1e-8aa7-5f355f6b5025)|
 
  |검색 기능|글 작성 내역 확인|
  |---|---|
  |![검색](https://github.com/user-attachments/assets/0a1fbc9e-b802-4b8a-8ecc-73e5d281fa0b)|![글 작성 내역](https://github.com/user-attachments/assets/65fa0fd9-d573-4c80-ab67-19ac6e48b437)|


  -----
  ### 배운 점 & 아쉬운 점
  ##### 배운점  
    1. 데이터가 많아질 때 어떻게 처리해야 할 지 기본적으로 이해할 수 있었다.
    2. 개인 정보를 처리할 때 안전하게 처리할 수 있는 방법을 알 게 되었다.
    3. Docker를 사용하여 배포를 간편하게 할 수 있었다.
    4. 만든 프로젝트를 유지하고 마이그레이션을 하는 방법도 알 수 있었다.
  ##### 아쉬운점
    1. 대용량 처리를 할 때 구현하려던 기술을 구현하지 못했다.
    2. 쿠키와 세션에 관한 부분을 고려하지 못했다.
    3. Docker를 사용한 것은 좋았지만 ci/cd 를 사용해서 자동 배포를 하지 못하였다.
### 참고한 내용
점프 투 스프링부트 https://wikidocs.net/book/7601

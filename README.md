## 캡스톤 디자인 팀 프로젝트

### 시스템 소개

- 공공데이터 API에서 데이터를 추출하여 학습시킨 인공지능 모델을 이용하여 버스 도착 예정 시간 예측
- GPS의 문제점을 보완하기 위해 비콘을 사용하여 사용자 위치 추정
- 무정차를 방지하기 위해 기사님에게 해당 정류장 승차 인원 정보 제공
- 버스 내 사고를 막기 위해 자리에 앉아서 애플리케이션 내 하차 버튼을 눌러 하차 예약
- 기사님에게 해당 정류장 하차 인원 정보 제공

----

### 팀 구성

<table style="width: 100%;">
  		<thead>
          <tr> 
      <th style="width: 30%;">직책(Position)</th> 
      <th style="width: 30%;">성명(Name)</th>
      <th style="width: 40%">역할(Role)</th>
    </tr>
  		</thead>
  		<tbody>
          <tr>
            <th>팀장</th>
            <th>이현수</th>
            <th>
              [IOS] 사용자 인터페이스 설계 및 애플리케이션 개발<br>
              애플리케이션 디자인
            </th>
          </tr>
          <tr>
            <th>팀원</th>
            <th>김진영</th>
            <th>
              [Docker] 서버 인프라<br>
              [Python] 데이터 전처리<br>
              인공지능 모델 설계 및 구현
            </th>
          </tr>
          <tr>
            <th>팀원</th>
            <th>임중훈</th>
            <th>
              [SpringBoot] 클라이언트와 서버 통신 환경 구축<br>
              데이터베이스 설계 및 관리<br>
              공공데이터 서버 통신<br>
              공공데이터 버스 데이터 가공<br>
              [Firebase] 서버 통신
            </th>
          </tr>
  		</tbody>
    </table>

-----

### 나의 역할

- Spring Security를 이용해 로그인 기능 구현
- API를 받아 데이터 추출
- 인공지능 서버에게 예측에 필요한 데이터 전달
- 공공데이터 서버에서 받은 버스 정보 데이터 가공
- 백엔드 비즈니스 로직 구현
- DB 설계 및 데이터 관리
- FCM을 이용해 버스 기사 애플리케이션에 알람 보내기

-----

### 아키텍처
- UML 다이어그램

![poster](./image/architecture.png)

- Data Flow Chart

|회원가입|로그인|
|:---:|:---:|
|<img src="/image/join.png" width="650" height="400" />|<img src="/image/login.png" width="650" height="400" />|
|승차예약|하차예약|
|<img src="/image/rideon.png" width="650" height="400" />|<img src="/image/rideoff.png" width="650" height="400" />|
|버스 정보 조회|예약 취소|
|<img src="/image/buslist.png" width="650" height="400" />|<img src="/image/cancel.png" width="650" height="400" />|
|운행 등록|정류장 정보 알림|
|<img src="/image/operation.png" width="650" height="400" />|<img src="/image/inform.png" width="650" height="400" />|

----

### 승객 애플리케이션
- 계정 생성 및 로그인

|<img src="/image/passengerAppImage/Picture.png" width="200" height="350" />|<img src="/image/passengerAppImage/Picture (1).png" width="200" height="350" />|<img src="/image/passengerAppImage/Picture (2).png" width="200" height="350" />|<img src="/image/passengerAppImage/Picture (3).png" width="200" height="350" />|
|:---:|:---:|:---:|:---:|
|Loading화면|FirstView|SignupView|LoginView|

- 노선 검색 기능

|<img src="/image/passengerAppImage/Picture (4).png" width="200" height="350" />|<img src="/image/passengerAppImage/Picture (5).png" width="200" height="350" />|<img src="/image/passengerAppImage/Picture (6).png" width="200" height="350" />|
|:---:|:---:|:---:|
|MainView|노선 검색 (search bar)|노선 검색 시 View|

- 승차 예약 기능

|<img src="/image/passengerAppImage/Picture (7).png" width="200" height="350" />|<img src="/image/passengerAppImage/Picture (8).png" width="200" height="350" />|<img src="/image/passengerAppImage/Picture (9).png" width="200" height="350" />|<img src="/image/passengerAppImage/Picture (10).png" width="200" height="350" />|
|:---:|:---:|:---:|:---:|
|Beacon인식|정류장 정보|승차 예약 Alert(1)|승차 예약 Alert(2)|

- 예약 현황 조회 및 하차 예약 기능

|<img src="/image/passengerAppImage/Picture (11).png" width="200" height="350" />|<img src="/image/passengerAppImage/Picture (12).png" width="200" height="350" />|<img src="/image/passengerAppImage/Picture (13).png" width="200" height="350" />|
|:---:|:---:|:---:|
|예약 현황View| 하차 예약 Alert(1)| 하차 예약 Alert(2)|

----

### 기사 애플리케이션

- 계정 생성 및 로그인

|<img src="/image/driverAppImage/Picture.png" width="200" height="350" />|<img src="/image/driverAppImage/Picture (1).png" width="200" height="350" />|<img src="/image/driverAppImage/Picture (2).png" width="200" height="350" />|
|:---:|:---:|:---:|
|FirstView|SignupView|LoginView|

- 버스 정보 입력 및 다음 정류장 정보 획득

|<img src="/image/driverAppImage/Picture (3).png" width="200" height="350" />|<img src="/image/driverAppImage/Picture (4).png" width="200" height="350" />|<img src="/image/driverAppImage/Picture (5).png" width="200" height="350" />|
|:---:|:---:|:---:|
|FirstView|SignupView|LoginView|


----

### 추가 문서

- [기능명세서](https://courageous-asteroid-4e0.notion.site/117b6db294bc81a593edf6b034e399d5?pvs=4)

- [API명세서](https://courageous-asteroid-4e0.notion.site/API-117b6db294bc81f3a162cf701fd3ae92?pvs=4)

- [학습 내용](https://www.notion.so/10cb6db294bc80d3aa39da3ae84345e3)

- [최종 논문](https://github.com/junghunim07/busthecall/blob/v1/develop/%E1%84%8C%E1%85%A9%E1%86%AF%E1%84%8B%E1%85%A5%E1%86%B8%E1%84%82%E1%85%A9%E1%86%AB%E1%84%86%E1%85%AE%E1%86%AB(191111%20%E1%84%8B%E1%85%B5%E1%86%B7%E1%84%8C%E1%85%AE%E1%86%BC%E1%84%92%E1%85%AE%E1%86%AB).pdf)

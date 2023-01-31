# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.2

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* 쓰레드로 http 요청을 동시에 처리하는데, chrome에서는 /index.html 요청에 한번더 /favicon.ico 요청을 더하고 있다.
* 이때 inputStream을 2개의 쓰레드가 공유하게 되는데, http 헤더를 읽을때 bufferedReader를 한쪽 쓰레드에서 close 시 소켓에러가 발생했다.
* bufferedReader는 쓰레드마다 새로 new로 인스턴스 생성을 하는데도, close 시에는 bufferedReader 인자로 받은 inputStream을 close하기 때문이다.

### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* connection 소켓 객체의 DataOutputStream에 302응답을 주려는데 오류..
* 원인은 위에 InputStream을 close 하면 바로 socket 이 closed 되기 때문에.
* Stream close는 로직 맨 마지막에 해야된다.

### 요구사항 5 - cookie
* ~/user/login 요청에 대해 Set-cookie를 받았으면
* ~/index.html 요청에는 Request Header에 cookie가 set 되지 않음.
* ~/user/ 내 요청에서만 cookie가 set됨.

### 요구사항 6 - 사용자목록 출력
* 성능 String << StringBuilder
* 

### 요구사항 7 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 

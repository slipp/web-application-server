# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* **HTTP Request & Response 구조**
  * **Request** 
    * | Request Line ( 라인 구성: HTTP-메서드 URI HTTP-버전 )
    * | Request Header
    * | Request Header 와 Request Body 사이의 빈 공백 라인
    * | Request Body
    <br/><br/>
  * **Response**
    * | State Line ( 라인 구성: HTTP-버전 상태코드 응답구문 )
    * | Response Header
    * | Response Header 와 Request Body 사이의 빈 공백 라인
    * | Response Body
    <br/><br/>
  * 각 Header와 Body 상세내용은 다를 수 있지만 전체적인 구조는 동일하게 구성 되어있다
  <br/><br/>
* **서버는 각 요청에 대하여 순차적으로 실행하는 것이 아님**
* **동시에 각 요청에 대응하는 스레드를 생성해 동시 실행함.**
  <br/><br/>
* **서버는 웹 페이지를 구성하는 모든 자원을 한번에 응답으로 보내지 않는다!**
  * 요청에 대한 처리 순서
    1. /index.html 요청에 대한 응답에 HTML을 보냄
    2. 응답 받은 브라우저는 HTML 내용을 분석해서 CSS, JAVASCRIPT, IMAGE 등 자원이 포함되어 있으면<br> 서버에 해당 자원을 다시 요청 
  * 그러므로
    * 하나의 웹페이지를 정상적으로 서비스 하려면 클라이언트와 서버 간 한번의 요청이 아닌<br>여러번의 요청과 응답을 주고 받게 됨.
    

### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
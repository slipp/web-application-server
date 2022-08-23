package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;


public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

			// 1.1 inputstream 한 줄로 읽기
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			// 1.2 라인별로 HTTP 요청 정보 읽어오기
			String line = br.readLine();

			log.debug("request line : {}", line);
			// 3.1 post의 경우 본문에 값이 담기므로 length를 0으로 초기화시켜준다.
			int contentLength = 0;
			// 1.3 line이 null값인 경우 예외 처리하기
			if (line == null) {
				return;
			}
			// 1.5 문자열 분리하기 ( GET /index.html HTTP/1.1 )
			String[] tokens = line.split(" ");

			// 6.1 사용자 목록에서 로그인 유무를 판단해서 보여주기 위한 변수
			boolean logined = false;

			// 1.4 헤더 마지막은 while문으로 확인이 가능하다.
			while (!line.equals("")) {
				log.debug("header line: {} ", line);
				line = br.readLine();
				if (line.contains("Cookie")) {
					logined = isLogin(line);
				}
				// 3.2 contentLength 읽어와서 가져오기
				if (line.contains("Content-Length")) {
					// 콘턴츠 길이 가져오기
					contentLength = getContentLength(line);
				}
			}
			// 2.1 HTTP 요청 첫 번째 라인에서 요청 URL을 추출한다.
			String url = tokens[1]; // user\create?userId=apple&....

			if ("/user/create".contains(url)) {
				// 3.3 본문 길이만큼 읽어오기
				String body = IOUtils.readData(br, contentLength);
				// 2.3 이름이랑 값 파싱하기 (필자가 구현한 API)
				Map<String, String> params = HttpRequestUtils.parseQueryString(body);
				// 2.4 User 객체에 저장해주기
				User user = new User(params.get("userId"), params.get("password"), params.get("name"),
						params.get("email"));

				log.debug("user: {}" , user);
				// 4.1 리다이렉트방식으로 페이지 이동하므로 302코드 이용하기
				DataOutputStream dos = new DataOutputStream(out);
				response302Header(dos, "/index.html");

				// 5.1 database에 user정보를 저장해둔다.
				DataBase.addUser(user);
				
				//7.1 css파일 읽어오기 
			} else if (url.endsWith(".css")) {
				DataOutputStream dos = new DataOutputStream(out);
				byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
				response200CssHeader(dos, body.length);
				responseBody(dos, body);
			}
			// 5.2 login버튼 눌렀을때 body 읽어오고 값 파싱하고 저장해둔 database에 userid 와 같으면 성공했음 알려주기
			else if ("/user/login".contains(url)) {
				String body = IOUtils.readData(br, contentLength);
				Map<String, String> params = HttpRequestUtils.parseQueryString(body);
				User user = DataBase.findUserById(params.get("userId"));
				if (user == null) {
					responseResource(out, "/user/login_failed.html");
				}
				if (user.getPassword().equals(params.get("password"))) {
					DataOutputStream dos = new DataOutputStream(out);
					response302LoginSuccessHeader(dos);
				} else {
					responseResource(out, "/user/login_failed.html");

				}
				// 6.1 사용자 목록 들어갔을때 목록 띄우기
			} else if ("/user/list".contains(url)) {
				if (!logined) {
					responseResource(out, "/user/login.html");
					return;

				}
				System.out.println("뭐지?");
				// 6.2 담아준 user 가져오기
				Collection<User> users = DataBase.findAll();
				log.debug("user: {}",users);
				// 6.3 StringBuilder 사용해서 출력하기
				StringBuilder sb = new StringBuilder();

				sb.append("<table border='1'>");
				for (User user : users) {
					sb.append("<tr>");
					sb.append("<td>" + user.getUserId() + "</td>");
					sb.append("<td>" + user.getName() + "</td>");
					sb.append("<td>" + user.getEmail() + "</td>");
					sb.append("</tr>");
				}
				sb.append("</table>");
				// 6.4 출력문 byte형식으로 변환해서 읽어오기
				byte[] body = sb.toString().getBytes();
				// 6.5 츨력스트림 열기
				DataOutputStream dos = new DataOutputStream(out);
				// 6.6 성공했으므로 200코드로 출력스트림이랑 body길이 보내기
				response200Header(dos, body.length);
				// 6.7 스트림 닫기
				responseBody(dos, body);
			}

			else {
				responseResource(out, url);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent+ "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
	}

	private boolean isLogin(String line) {
		String[] headerTokens = line.split(":");
		Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
		String value = cookies.get("logined");

		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	// 로그인 성공했을 경우 리다이렉트로 이동하게 해줌.
	private void response302LoginSuccessHeader(DataOutputStream dos) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Set-Cookie: logined=true \r\n");
			dos.writeBytes("Location: /index.html" + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());

		}
	}

	// 파일 읽고 응답해주는 메소드
	private void responseResource(OutputStream out, String url) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
		response200Header(dos, body.length);
		responseBody(dos, body);

	}

	private int getContentLength(String line) {
		// line : Content-length:59 // headertokens {Content-length, 59}
		String[] headertokens = line.split(":");
		return Integer.parseInt(headertokens[1].trim()); // 59
	}

	// 2xx: 성공
	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	// 3xx : 리다이렉션(요청을 마치기 위해 추가 동작이 필요하다)
	private void response302Header(DataOutputStream dos, String url) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: " + url + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
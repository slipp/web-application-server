package webserver;

//객체를 최대한 활용하려고 연습하기 위해서는 객체에서 값을 꺼낸 후 로직을 구현하려고 하지말고
//값을 가지고 있는 객체에 메세지를 보내 일ㅇ릉 시키도록 연습하는 것이 좋다. 
//이 메소드도 get, post 값을 꺼내 비교하는 것이아니라 이 값을 가지고 있는 
//Httpmethod가 Post여부를 판단하도록 메세지를 보내 물어보고 있다. 

public enum HttpMethod {
	GET,
	POST;
	
	
	public boolean isPost() {
		return this == POST;
	}
}

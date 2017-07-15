package biz.aQute.openapi.oauth2.provider;

import org.junit.Test;

import aQute.www.http.util.HttpRequest;

public class Testing {

	@Test
	public void testclient() {
		byte[] bytes = HttpRequest.get("https://google.com").bytes();
		System.out.println(bytes.length);
	}
}

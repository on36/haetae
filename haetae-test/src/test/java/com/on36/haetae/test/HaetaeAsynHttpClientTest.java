package com.on36.haetae.test;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.asynchttpclient.request.body.multipart.StringPart;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketTextListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhanghr
 * @date 2016年1月3日
 */
public class HaetaeAsynHttpClientTest {

	@Test
	public void testHello() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/hello").execute()
				.get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("Hello xiongdi!", result);
		asyncHttpClient.close();
	}

	@Test
	public void testName() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/name/zhangsan")
				.execute().get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("Hello zhangsan", result);
		asyncHttpClient.close();
	}

	@Test
	public void testMulti() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/multi/zhangsan/123")
				.execute().get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("Hello zhangsan 123", result);
		asyncHttpClient.close();
	}

	@Test
	public void testHeaderValue() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/greeting").execute()
				.get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("Hello NING/1.0", result);
		asyncHttpClient.close();
	}

	@Test
	public void testRequestControl() throws Exception {

		int count = 20;

		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		while (count-- > 0) {
			long start = System.currentTimeMillis();
			Response resp = asyncHttpClient
					.prepareGet("http://localhost:8080/services/control")
					.execute().get();
			System.out.println(System.currentTimeMillis() - start);
			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals(
					"{\"status\":200,\"message\":\"OK\",\"result\":\"Hello control!\"}",
					result);
		}
		asyncHttpClient.close();
	}

	@Test
	public void testBlackList() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/black").execute()
				.get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("Hello black!", result);
		asyncHttpClient.close();
	}

	@Test
	public void testWhiteList() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/white").execute()
				.get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("Hello white!", result);
		asyncHttpClient.close();
	}

	@Test
	public void testWhiteListAndRequestControl() throws Exception {

		int count = 20;

		while (count-- > 0) {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
					.prepareGet("http://localhost:8080/services/whitecontrol")
					.execute().get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("Hello white!", result);
			asyncHttpClient.close();
		}
	}

	@Test
	public void testBodyParts() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/custom")
				.addBodyPart(new StringPart("user", "zhangsan"))
				.addBodyPart(new StringPart("name", "nihao")).execute().get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("nihao", result);
		asyncHttpClient.close();
	}

	@Test
	public void testBodyParameter() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/custom")
				.addQueryParam("user", "zhangsan")
				.addQueryParam("name", "nihao").execute().get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("zhangsan", result);
		asyncHttpClient.close();
	}

	@Test
	public void testSplatParameter() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost(
						"http://localhost:8080/services/custom?user=zhangsan&name=nihao")
				.execute().get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("zhangsan", result);
		asyncHttpClient.close();
	}

	@Test
	public void testBodyString() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/body")
				.setHeader("Content-Type", "application/json")
				.setBody("{\"val\":\"someJSON\"}").execute().get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("{\"val\":\"someJSON\"}", result);
		asyncHttpClient.close();
	}

	@Test
	public void testBodyObejct() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/customobject")
				.setHeader("Content-Type", "application/json")
				.setBody("{\"val\":\"someJSON\"}").execute().get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("someJSON", result);
		asyncHttpClient.close();
	}

	@Test
	public void testCapturedParameter() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost(
						"http://localhost:8080/services/custombody/lisi/zhangsan")
				.execute().get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("lisi zhangsan", result);
		asyncHttpClient.close();
	}

	@Test
	public void testTimeout() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/timeout").execute()
				.get();

		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("lisi zhangsan", result);
		asyncHttpClient.close();
	}

	@Test
	public void testWS() throws Exception {
		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
		asyncHttpClient.prepareGet("ws://localhost:8080/ws")
				.execute(new WebSocketUpgradeHandler.Builder()
						.addWebSocketListener(new WebSocketTextListener() {

							@Override
							public void onOpen(WebSocket websocket) {
								// TODO Auto-generated method stub
								websocket.sendMessage("hello server");
							}

							@Override
							public void onError(Throwable t) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onClose(WebSocket websocket) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onMessage(String message) {
								// TODO Auto-generated method stub
								System.out.println(message);
							}
						}).build())
				.get();

		asyncHttpClient.close();
	}

}

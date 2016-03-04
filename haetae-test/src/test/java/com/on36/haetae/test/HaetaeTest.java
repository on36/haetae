package com.on36.haetae.test;

import org.junit.Assert;
import org.junit.Test;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.ning.http.multipart.StringPart;


/**
 * @author zhanghr
 * @date 2016年1月3日
 */
public class HaetaeTest {

	@Test
	public void testHello() throws Exception {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/hello")
				.execute().get();
		
		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("Hello xiongdi!", result);
		asyncHttpClient.close();
	}
	@Test
	public void testName() throws Exception {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
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
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
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
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/greeting")
				.execute().get();
		
		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("Hello NING/1.0", result);
		asyncHttpClient.close();
	}
	@Test
	public void testBlackList() throws Exception {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/black")
				.execute().get();
		
		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("Hello black!", result);
		asyncHttpClient.close();
	}
	@Test
	public void testWhiteList() throws Exception {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/white")
				.execute().get();
		
		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("Hello white!", result);
		asyncHttpClient.close();
	}
	@Test
	public void testBodyParts() throws Exception {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/custom")
				.addBodyPart(new StringPart("user", "zhangsan"))
				.addBodyPart(new StringPart("name", "nihao"))
				.execute().get();
		
		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("nihao", result);
		asyncHttpClient.close();
	}
	@Test
	public void testBodyParameter() throws Exception {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/custom")
				.addParameter("user", "zhangsan")
				.addParameter("name", "nihao")
				.execute().get();
		
		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("zhangsan", result);
		asyncHttpClient.close();
	}
	@Test
	public void testSplatParameter() throws Exception {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/custom?user=zhangsan&name=nihao")
				.execute().get();
		
		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("zhangsan", result);
		asyncHttpClient.close();
	}
	@Test
	public void testCapturedParameter() throws Exception {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/custombody/lisi/zhangsan")
				.execute().get();
		
		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("lisi zhangsan", result);
		asyncHttpClient.close();
	}
}

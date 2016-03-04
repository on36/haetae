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
	public void testBodyParts() throws Exception {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/custom")
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
				.addParameter("name", "wangwu")
				.execute().get();
		
		String result = resp.getResponseBody().trim();
		System.out.println(result);
		Assert.assertEquals("wangwu", result);
		asyncHttpClient.close();
	}
	@Test
	public void testSplatParameter() throws Exception {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/custom?name=zhangsan")
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

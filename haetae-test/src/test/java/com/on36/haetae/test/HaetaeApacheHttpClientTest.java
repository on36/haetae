package com.on36.haetae.test;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * @author zhanghr
 * @date 2016年1月6日
 */
public class HaetaeApacheHttpClientTest {

	@Test
	public void testHello() throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("http://localhost:8080/services/hello");
		HttpResponse response = client.execute(request);
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			System.out.println(EntityUtils.toString(response.getEntity()));
		}
	}
}

package com.on36.haetae.test;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Response;

/**
 * @author zhanghr
 * @date 2016年8月23日
 */
public class Test {

	public static void main(String[] args) throws Exception {

		AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(
				new DefaultAsyncHttpClientConfig.Builder()
						.setPooledConnectionIdleTimeout(5000)
						.setRequestTimeout(5000).build());// 请求5S超时
		Response resp = asyncHttpClient
				.preparePut("http://localhost:8080/services/user/").addHeader("VERSION", "6.1")
				.execute().get();
		String result = resp.getResponseBody().trim();
		System.out.println(result);
		asyncHttpClient.close();
	}
}

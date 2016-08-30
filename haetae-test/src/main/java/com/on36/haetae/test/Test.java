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
		int i = 1000;
		while (i-- > 0) {
			long start = System.currentTimeMillis();
			Response resp = asyncHttpClient
					.prepareGet("http://192.168.153.129:8080/services/hello")
					.execute().get();
			System.out.println(System.currentTimeMillis() - start);
			String result = resp.getResponseBody().trim();
			System.out.println(result);
			long sleep = (long) (3000 + Math.random()* 5000);
			System.out.println("sleeping " +sleep +"ms");
			Thread.sleep(sleep);
		}
		asyncHttpClient.close();
	}
}

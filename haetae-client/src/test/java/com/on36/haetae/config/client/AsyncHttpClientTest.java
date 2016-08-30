package com.on36.haetae.config.client;

/**
 * @author zhanghr
 * @date 2016年8月25日 
 */
public class AsyncHttpClientTest {

	public static void main(String[] args) {
		try {
			HttpClient.getInstance().get("/hello");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpClient.getInstance().close();
	}
}

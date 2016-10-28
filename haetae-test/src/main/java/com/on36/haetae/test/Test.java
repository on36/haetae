package com.on36.haetae.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.on36.haetae.api.JSONObject;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.config.client.HttpClient;

/**
 * @author zhanghr
 * @date 2016年6月23日
 */
public class Test {

	public static void main(String[] args) throws Exception {
		//
		// HttpClient.getAsyncHttpClient().prepareGet("ws://localhost:1025/ws")
		// .execute(new WebSocketUpgradeHandler.Builder()
		// .addWebSocketListener(new WebSocketTextListener() {
		//
		// @Override
		// public void onOpen(WebSocket websocket) {
		// // TODO Auto-generated method stub
		// websocket.sendMessage(
		// "shanghai://127.0.0.1:8888");
		// }
		//
		// @Override
		// public void onError(Throwable t) {
		// // TODO Auto-generated method stub
		//
		// System.out.println("onError");
		// }
		//
		// @Override
		// public void onClose(WebSocket websocket) {
		// // TODO Auto-generated method stub
		// System.out.println("onClose");
		// }
		//
		// @Override
		// public void onMessage(String message) {
		// // TODO Auto-generated method stub
		// System.out.println(message);
		// }
		// }).build())
		// .get();
//		Map<String, String> header = new HashMap<String, String>();
//		header.put("Content-Type", MediaType.TEXT_JSON.value());
//		JSONObject result = HttpClient.getInstance()
//				.getJSON("http://192.168.153.129:8080/shanghai", null, header);
//		System.out.println(result.getList(String.class, "result"));

		Thread current = Thread.currentThread();
		System.out.println(current.getPriority());
		System.out.println(current.getName());
		System.out.println(current.activeCount());
		System.out.println(current.getId());
		System.out.println(current.getThreadGroup());
		System.out.println(current.getStackTrace());
		System.out.println(current.hashCode());
		System.out.println(current.toString());
		
		System.setProperty("java.class.path","../lib/cust-00.1jar;../lib/jetty-runner-201923234.jar;../lib/log4j-2.0.1.jar");
		String classPath = System.getProperty("java.class.path");
		int index = classPath.indexOf("../lib/jetty-runner");
		if (index > -1) {
			String jettyjar = classPath.substring(index,
					classPath.indexOf(File.pathSeparator, index));
			System.setProperty("java.class.path",
					classPath.replace(jettyjar, ""));
			System.out.println( System.getProperty("java.class.path"));
		}
		
		System.out.println(String.format("/apps/%s","crm"));
	}
}

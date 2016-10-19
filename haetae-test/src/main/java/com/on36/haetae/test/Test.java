package com.on36.haetae.test;

import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketTextListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;

import com.on36.haetae.config.client.HttpClient;

/**
 * @author zhanghr
 * @date 2016年6月23日
 */
public class Test {

	public static void main(String[] args) throws Exception {

		HttpClient.getAsyncHttpClient().prepareGet("ws://localhost:1025/ws")
				.execute(new WebSocketUpgradeHandler.Builder()
						.addWebSocketListener(new WebSocketTextListener() {

							@Override
							public void onOpen(WebSocket websocket) {
								// TODO Auto-generated method stub
								websocket.sendMessage(
										"shanghai://127.0.0.1:8888");
							}

							@Override
							public void onError(Throwable t) {
								// TODO Auto-generated method stub

								System.out.println("onError");
							}

							@Override
							public void onClose(WebSocket websocket) {
								// TODO Auto-generated method stub
								System.out.println("onClose");
							}

							@Override
							public void onMessage(String message) {
								// TODO Auto-generated method stub
								System.out.println(message);
							}
						}).build())
				.get();
	}
}

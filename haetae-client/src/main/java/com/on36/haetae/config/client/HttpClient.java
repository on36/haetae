package com.on36.haetae.config.client;

import java.io.IOException;
import java.util.Map;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Response;

import com.on36.haetae.api.JSONObject;
import com.on36.haetae.common.conf.Configuration;
import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.common.utils.ThrowableUtils;
import com.on36.haetae.config.client.json.JSONObjectImpl;
import com.on36.haetae.config.client.json.util.JSONUtils;

/**
 * @author zhanghr
 * @date 2016年4月19日
 */
public class HttpClient {

	private static class HttpClientHolder {
		private static HttpClient instance = new HttpClient();
		private static AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(
				new DefaultAsyncHttpClientConfig.Builder()
						.setKeepAlive(Configuration.create().getBoolean(
								Constant.K_HTTPCLIENT_KEEPALIVE,
								Constant.V_HTTPCLIENT_KEEPALIVE))
						.setPooledConnectionIdleTimeout(Configuration.create()
								.getInt(Constant.K_HTTPCLIENT_CONNECTION_IDLE_TIMEOUT,
										Constant.V_HTTPCLIENT_CONNECTION_IDLE_TIMEOUT))
						.setConnectionTtl(Configuration.create().getInt(
								Constant.K_HTTPCLIENT_CONNECTION_TTL,
								Constant.V_HTTPCLIENT_CONNECTION_TTL))
						.setRequestTimeout(Configuration.create().getInt(
								Constant.K_HTTPCLIENT_REQUEST_TIMEOUT,
								Constant.V_HTTPCLIENT_REQUEST_TIMEOUT))
						.build());
	}

	private HttpClient() {
	}

	public static HttpClient getInstance() {
		return HttpClientHolder.instance;
	}

	public String post(String url) throws Exception {
		return post(url, null);
	}

	public String post(String url, Map<String, String> queryParam)
			throws Exception {
		return post(url, queryParam, null);
	}

	public String post(String url, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {
		Response resp = null;
		try {
			BoundRequestBuilder request = HttpClientHolder.asyncHttpClient
					.preparePost(url);
			if (header != null) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					request.setHeader(key, value);
				}
			}
			if (queryParam != null) {
				for (Map.Entry<String, String> entry : queryParam.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					request.addQueryParam(key, value);
				}
			}
			resp = request.execute().get();
		} catch (Exception e) {
		}
		if (resp != null && resp.getStatusCode() == 200)
			return resp.getResponseBody().trim();
		else if (resp != null)
			throw new Exception("post " + url + " failed !",
					ThrowableUtils.makeThrowable(JSONUtils.get(String.class,
							resp.getResponseBody().trim(), "result")));
		else
			throw new Exception("post " + url + " failed !");
	}

	public String postBody(String url, String body) throws Exception {
		return postBody(url, body, null);
	}

	public String postBody(String url, String body, Map<String, String> header)
			throws Exception {
		Response resp = null;
		try {
			BoundRequestBuilder request = HttpClientHolder.asyncHttpClient
					.preparePost(url);
			if (header != null) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					request.setHeader(key, value);
				}
			}
			if (body != null)
				request.setBody(body);

			resp = request.execute().get();
		} catch (Exception e) {
		}
		if (resp != null && resp.getStatusCode() == 200)
			return resp.getResponseBody().trim();
		else if (resp != null)
			throw new Exception("getbody " + url + " failed !",
					ThrowableUtils.makeThrowable(JSONUtils.get(String.class,
							resp.getResponseBody().trim(), "result")));
		else
			throw new Exception("getbody " + url + " failed !");
	}

	public JSONObject post2JSON(String url) throws Exception {

		return new JSONObjectImpl(post(url));
	}

	public JSONObject post2JSON(String url, String body) throws Exception {

		return new JSONObjectImpl(postBody(url, body));
	}

	public JSONObject post2JSON(String url, String body,
			Map<String, String> header) throws Exception {

		return new JSONObjectImpl(postBody(url, body, header));
	}

	public JSONObject post2JSON(String url, Map<String, String> queryParam)
			throws Exception {

		return new JSONObjectImpl(post(url, queryParam));
	}

	public JSONObject post2JSON(String url, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {

		return new JSONObjectImpl(post(url, queryParam, header));
	}

	public String get(String url) throws Exception {

		return get(url, null);
	}

	public String get(String url, Map<String, String> queryParam)
			throws Exception {

		return get(url, null, null);
	}

	public String get(String url, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {
		Response resp = null;
		try {
			BoundRequestBuilder request = HttpClientHolder.asyncHttpClient
					.prepareGet(url);
			if (header != null) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					request.setHeader(key, value);
				}
			}
			if (queryParam != null) {
				for (Map.Entry<String, String> entry : queryParam.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					request.addQueryParam(key, value);
				}
			}
			resp = request.execute().get();
		} catch (Exception e) {
		}
		if (resp != null && resp.getStatusCode() == 200)
			return resp.getResponseBody().trim();
		else if (resp != null)
			throw new Exception("get " + url + " failed !",
					ThrowableUtils.makeThrowable(JSONUtils.get(String.class,
							resp.getResponseBody().trim(), "result")));
		else
			throw new Exception("get " + url + " failed !");
	}

	public JSONObject get2JSON(String url) throws Exception {

		return get2JSON(url, null, null);
	}

	public JSONObject get2JSON(String url, Map<String, String> queryParam)
			throws Exception {

		return get2JSON(url, queryParam, null);
	}

	public JSONObject get2JSON(String url, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {

		return new JSONObjectImpl(get(url, queryParam, header));
	}

	public void close() {
		try {
			if (!HttpClientHolder.asyncHttpClient.isClosed())
				HttpClientHolder.asyncHttpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

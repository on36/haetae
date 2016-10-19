package com.on36.haetae.config.client;

import java.io.IOException;
import java.util.Map;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;

import com.on36.haetae.api.http.MethodType;
import com.on36.haetae.common.conf.Configuration;
import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.common.utils.ThrowableUtils;
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
	public static AsyncHttpClient getAsyncHttpClient() {
		return HttpClientHolder.asyncHttpClient;
	}

	public String put(String url) throws Exception {
		return put(url, null);
	}

	public String put(String url, Map<String, String> queryParam)
			throws Exception {
		return put(url, queryParam, null);
	}

	public String put(String url, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {
		return send(url, MethodType.PUT, queryParam, null, header);
	}

	public String get(String url) throws Exception {
		return get(url, null);
	}

	public String get(String url, Map<String, String> queryParam)
			throws Exception {
		return get(url, queryParam, null);
	}

	public String get(String url, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {
		return send(url, MethodType.GET, queryParam, null, header);
	}

	public String delete(String url) throws Exception {
		return delete(url, null);
	}

	public String delete(String url, Map<String, String> queryParam)
			throws Exception {
		return delete(url, queryParam, null);
	}

	public String delete(String url, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {
		return send(url, MethodType.DELETE, queryParam, null, header);
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
		return send(url, MethodType.POST, queryParam, null, header);
	}

	public String postBody(String url, String body) throws Exception {
		return postBody(url, body, null);
	}

	public String postBody(String url, String body, Map<String, String> header)
			throws Exception {
		return send(url, MethodType.POST, null, body, header);
	}

	public String send(String url, MethodType method,
			Map<String, String> queryParam, String body,
			Map<String, String> header) throws Exception {
		Response resp = null;
		try {
			BoundRequestBuilder request = HttpClientHolder.asyncHttpClient
					.prepareRequest(new RequestBuilder(method.name()))
					.setUrl(url);

			if (header != null) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					request.setHeader(key, value);
				}
			}
			if (body != null) {
				request.setBody(body);
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
		else if (resp != null) {
			String responseBody = resp.getResponseBody().trim();
			String error = JSONUtils.get(String.class, responseBody, "result");
			if (error != null)
				throw new Exception(method.name() + " " + url + " failed !",
						ThrowableUtils.makeThrowable(error));
			else
				throw new Exception(responseBody);
		} else
			throw new Exception(method.name() + " " + url + " failed !");
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

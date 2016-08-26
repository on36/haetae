package com.on36.haetae.config.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Response;

import com.on36.haetae.api.JSONObject;
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
						.setRequestTimeout(5000).build());// 请求5S超时
	}

	private HttpClient() {
	}

	public static HttpClient getInstance() {
		return HttpClientHolder.instance;
	}

	private String getURI(String path) {
		if (path != null && path.startsWith("http"))
			return path;
		List<String> address = ConfigClient.getList("serice");
		return address.get(0);
	}

	public String post(String uri) throws Exception {
		return post(uri, null);
	}

	public String post(String uri, Map<String, String> queryParam)
			throws Exception {
		return post(uri, queryParam, null);
	}

	public String post(String uri, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {
		BoundRequestBuilder request = HttpClientHolder.asyncHttpClient
				.preparePost(getURI(uri));
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
		Response resp = request.execute().get();
		if (resp.getStatusCode() == 200)
			return resp.getResponseBody().trim();
		else
			throw new Exception(resp.getResponseBody().trim());
	}

	public String postBody(String uri, String body) throws Exception {
		return postBody(uri, body, null);
	}

	public String postBody(String uri, String body, Map<String, String> header)
			throws Exception {
		Response resp = null;
		try {
			BoundRequestBuilder request = HttpClientHolder.asyncHttpClient
					.preparePost(getURI(uri));
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
			e.printStackTrace();
		}
		if (resp.getStatusCode() == 200)
			return resp.getResponseBody().trim();
		else
			throw new Exception(resp.getResponseBody().trim());
	}

	public <T> T post2Entity(String uri, Class<T> clazz) throws Exception {

		return JSONUtils.fromJson(clazz, post(uri));
	}

	public <T> T post2Entity(String uri, String body, Class<T> clazz)
			throws Exception {

		return JSONUtils.fromJson(clazz, postBody(uri, body));
	}

	public <T> T post2Entity(String uri, String body,
			Map<String, String> header, Class<T> clazz) throws Exception {

		return JSONUtils.fromJson(clazz, postBody(uri, body, header));
	}

	public <T> T post2Entity(String uri, Map<String, String> queryParam,
			Class<T> clazz) throws Exception {

		return JSONUtils.fromJson(clazz, post(uri, queryParam));
	}

	public <T> T post2Entity(String uri, Map<String, String> queryParam,
			Map<String, String> header, Class<T> clazz) throws Exception {

		return JSONUtils.fromJson(clazz, post(uri, queryParam, header));
	}

	public JSONObject post2JSON(String uri) throws Exception {

		return new JSONObjectImpl(post(uri));
	}

	public JSONObject post2JSON(String uri, String body) throws Exception {

		return new JSONObjectImpl(postBody(uri, body));
	}

	public JSONObject post2JSON(String uri, String body,
			Map<String, String> header) throws Exception {

		return new JSONObjectImpl(postBody(uri, body, header));
	}

	public JSONObject post2JSON(String uri, Map<String, String> queryParam)
			throws Exception {

		return new JSONObjectImpl(post(uri, queryParam));
	}

	public JSONObject post2JSON(String uri, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {

		return new JSONObjectImpl(post(uri, queryParam, header));
	}

	public String get(String uri) throws Exception {

		return get(uri, null);
	}

	public String get(String uri, Map<String, String> queryParam)
			throws Exception {

		return get(uri, null, null);
	}

	public String get(String uri, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {
		Response resp = null;
		try {
			BoundRequestBuilder request = HttpClientHolder.asyncHttpClient
					.prepareGet(getURI(uri));
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
			e.printStackTrace();
		}
		if (resp.getStatusCode() == 200)
			return resp.getResponseBody().trim();
		else
			throw new Exception(resp.getResponseBody().trim());
	}

	public JSONObject get2JSON(String uri) throws Exception {

		return get2JSON(uri, null, null);
	}

	public JSONObject get2JSON(String uri, Map<String, String> queryParam)
			throws Exception {

		return get2JSON(uri, queryParam, null);
	}

	public JSONObject get2JSON(String uri, Map<String, String> queryParam,
			Map<String, String> header) throws Exception {

		return new JSONObjectImpl(get(uri, queryParam, header));
	}

	public <T> T get2Entity(String uri, Class<T> clazz) throws Exception {

		return get2Entity(uri, null, clazz);
	}

	public <T> T get2Entity(String uri, Map<String, String> queryParam,
			Class<T> clazz) throws Exception {

		return get2Entity(uri, queryParam, null, clazz);
	}

	public <T> T get2Entity(String uri, Map<String, String> queryParam,
			Map<String, String> header, Class<T> clazz) throws Exception {

		return JSONUtils.fromJson(clazz, get(uri, queryParam, header));
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

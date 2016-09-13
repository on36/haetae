package com.on36.haetae.api;

import java.util.Map;
import java.util.Set;

import com.on36.haetae.api.http.Session;

public interface Context {

	/**
	 * 请求客户端IP.
	 * 
	 * @return
	 */
	String getClientIP();
	/**
	 * 请求客户端端口.
	 * 
	 * @return
	 */
	int getClientPort();
	/**
	 * 请求客户端地址 IP+端口.
	 * 
	 * @return
	 */
	String getClientAddress();

	/**
	 * 请求开始响应时间
	 * 
	 * @return
	 */
	long getStartHandleTime();

	/**
	 * 返回请求链ID
	 * 
	 * @return
	 */
	String getTraceId();

	/**
	 * 返回请求链当前父节点ID
	 * 
	 * @return
	 */
	String getParentId();

	/**
	 * 返回请求链当前节点ID
	 * 
	 * @return
	 */
	String getSpanId();

	/**
	 * 请求URI路径
	 * 
	 * @return
	 */
	String getPath();

	/**
	 * 根据参数名获取请求参数的值
	 * 
	 * @param param
	 *            参数名
	 * @return
	 */
	String getRequestParameter(String param);

	/**
	 * 返回所有请求参数名的集合
	 * 
	 * @return
	 */
	Set<String> getRequestParameterNames();

	/**
	 * 返回所有请求参数key-value的集合
	 * 
	 * @return
	 */
	Map<String, String> getRequestParameters();

	/**
	 * 返回捕获匹配的参数值，如 /user/:id 需要获取:id的值
	 * 
	 * @param captured
	 *            匹配表达式 如 :id
	 * @return
	 */
	String getCapturedParameter(String captured);

	/**
	 * 返回获取请求头的参数值
	 * 
	 * @param param
	 *            请求头参数名
	 * @return
	 */
	String getHeaderValue(String param);

	/**
	 * 返回获取请求头Content-Length的参数值
	 * 
	 * @return
	 */
	long getContentLength();

	/**
	 * 返回获取请求头Content-Type的参数值
	 * 
	 * @return
	 */
	String getContenType();

	/**
	 * 返回获取Session对象，如果没有，则返回null
	 * 
	 * @return
	 */
	Session getSession();

	/**
	 * 返回获取请求体的值，返回字符串类型
	 * 
	 * @return
	 */
	String getBodyAsString();

	/**
	 * 返回获取请求体的值，返回JSON对象
	 * 
	 * @return
	 */
	JSONObject getBodyAsJSONObject();

	/**
	 * 返回获取请求体的值对象，如果请求体数据是JSON对象格式，否则抛出异常
	 * 
	 * @param clazz
	 *            返回对象类型
	 * @return
	 */
	<T> T getBodyAsEntity(Class<T> clazz);

	/**
	 * 调用其他请求，返回字符串结果
	 * 
	 * @param resource
	 *            请求的URI路径
	 * @return
	 * @throws Exception
	 */
	String getURI(String resource) throws Exception;

	String getURI(String resource, Map<String, String> queryParam)
			throws Exception;

	/**
	 * 调用其他请求，如果结果数据是JSON对象格式，返回获取请求体的值对象，否则抛出异常
	 * 
	 * @param resource
	 *            请求的URI路径
	 * @param clazz
	 *            返回对象类型
	 * @return
	 * @throws Exception
	 */
	<T> T getURI(String resource, Class<T> clazz) throws Exception;

	/**
	 * 调用其他请求，返回字符串结果
	 * 
	 * @param resource
	 *            请求的URI路径
	 * @return
	 * @throws Exception
	 */
	String postURI(String resource) throws Exception;

	String postURI(String resource, String body) throws Exception;

	String postURI(String resource, Map<String, String> queryParam)
			throws Exception;

	/**
	 * 调用其他请求，如果结果数据是JSON对象格式，返回获取请求体的值对象，否则抛出异常
	 * 
	 * @param resource
	 *            请求的URI路径
	 * @param clazz
	 *            返回对象类型
	 * @return
	 * @throws Exception
	 */
	<T> T postURI(String resource, Class<T> clazz) throws Exception;

	<T> T postURI(String resource, String body, Class<T> clazz)
			throws Exception;

	<T> T postURI(String resource, Map<String, String> queryParam,
			Class<T> clazz) throws Exception;

	/**
	 * 异步输出日志,仅仅用于用户自定义的服务类中使用，其它地方使用可能带来上下文信息不准确
	 * 
	 * @param level
	 *            日志级别 ,可选值有：INFO、WARN、ERROR、DEBUG
	 * @param message
	 *            输出信息
	 * @param t
	 *            异常
	 */
	void trace(String level, String message, Throwable t);
	void trace(String level, String message);
}
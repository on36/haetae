package com.on36.haetae.http;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import com.on36.haetae.api.core.HttpHandler;

/**
 * @author zhanghr
 * @date 2015年12月30日
 */
public interface RequestHandler {

	/**
	 * 指定响应的结果body，如果有自定义的处理逻辑，则设置无效.
	 * 
	 * @param body
	 *            响应结果
	 * @return
	 */
	RequestHandler with(String body);

	/**
	 * 指定自定义处理逻辑实现HttpHandler接口的实现类,如果有Object和Method自定义的处理逻辑，则设置无效.
	 * 
	 * @see com.on36.haetae.api.core.HttpHandler
	 * 
	 * @param customHandler
	 *            自定义处理逻辑实现类
	 * @return
	 */
	RequestHandler with(HttpHandler<?> customHandler);

	/**
	 * 指定自定义处理逻辑实现实现类.
	 * 
	 * @param object
	 *            自定义处理逻辑实现类
	 * @param method
	 *            自定义处理逻辑的方法
	 * @return
	 */
	RequestHandler with(Object object, Method method);

	/**
	 * 指定一个响应头的参数.
	 * 
	 * @param name
	 *            参数名
	 * @param value
	 *            参数值
	 * @return
	 */
	RequestHandler withHeader(String name, String value);

	/**
	 * 指定响应的超时时间.
	 * 
	 * @param timeout
	 *            超时时间
	 * @param timeoutUnit
	 *            时间单位
	 * @return
	 */
	RequestHandler timeout(long timeout, TimeUnit timeoutUnit);

	/**
	 * 指定单位时间内的全局最大请求次数.
	 * 
	 * @param period
	 *            时间数
	 * @param periodUnit
	 *            时间单位
	 * @param times
	 *            请求次数
	 * @return
	 */
	RequestHandler every(long period, TimeUnit periodUnit, int times);

	/**
	 * 指定一秒钟内的全局最大请求次数.
	 * 
	 * @param times
	 *            请求次数
	 * @return
	 */
	RequestHandler every(int times);

	/**
	 * 设置是否需要做黑白名单和流量权限控制,默认为true.
	 * 
	 * @param authentication
	 * @return
	 */
	RequestHandler auth(boolean authentication);

	/**
	 * 设置是否需要数据签名验证,默认为true.
	 * 
	 * @param verify
	 * @return
	 */
	RequestHandler verify(boolean verify);

	/**
	 * 删除黑名单的IP限制.
	 * 
	 * @param blackips
	 *            ip地址
	 * @return
	 */
	RequestHandler unban(String... blackips);

	/**
	 * 增加黑名单的IP限制.
	 * 
	 * @param blackips
	 *            ip地址
	 * @return
	 */
	RequestHandler ban(String... blackips);

	/**
	 * 删除白名单的IP限制.
	 * 
	 * @param whiteips
	 *            ip地址
	 * @return
	 */
	RequestHandler unpermit(String... whiteips);

	/**
	 * 增加白名单的IP限制.
	 * 
	 * @param whiteips
	 *            ip地址
	 * @return
	 */
	RequestHandler permit(String... whiteips);

	/**
	 * 增加白名单的IP限制，设置一秒的服务等级.
	 * 
	 * @param ip
	 *            ip地址
	 * @param level
	 *            服务等级
	 * @return
	 */
	RequestHandler permit(String ip, ServiceLevel level);

	/**
	 * 增加白名单的IP限制，设置一秒最大请求数;但是不能超过every设置的流量限量，否则以every设置为准.
	 * 
	 * @param ip
	 *            ip地址
	 * @param times
	 *            请求数
	 * @return
	 */
	RequestHandler permit(String ip, int times);

	/**
	 * 增加白名单的IP限制，设置单位时间内的服务等级;但是不能超过every设置的流量限量，否则以every设置为准.
	 * 
	 * @param ip
	 *            ip地址
	 * @param level
	 *            服务等级
	 * @param period
	 *            时间
	 * @param periodUnit
	 *            时间单位
	 * @return
	 */
	RequestHandler permit(String ip, ServiceLevel level, long period,
			TimeUnit periodUnit);

	/**
	 * 增加白名单的IP限制，设置单位时间内的最大请求数;但是不能超过every设置的流量限量，否则以every设置为准.
	 * 
	 * @param ip
	 *            ip地址
	 * @param times
	 *            请求数
	 * @param period
	 *            时间
	 * @param periodUnit
	 *            时间单位
	 * @return
	 */
	RequestHandler permit(String ip, int times, long period,
			TimeUnit periodUnit);

	/**
	 * 设置转发地址.
	 * 
	 * @param location
	 *            转发地址
	 * @return
	 */
	RequestHandler withRedirect(String location);

	/**
	 * 设置是否产生session.
	 * 
	 * @param has
	 * @return
	 */
	RequestHandler session(boolean has);
}

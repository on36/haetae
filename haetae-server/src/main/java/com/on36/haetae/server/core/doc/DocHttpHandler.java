package com.on36.haetae.server.core.doc;

import java.util.Map;
import java.util.TreeMap;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Api;
import com.on36.haetae.api.annotation.ApiDoc;
import com.on36.haetae.api.annotation.ApiParam;
import com.on36.haetae.api.core.HttpHandler;
import com.on36.haetae.http.HandlerKey;
import com.on36.haetae.server.core.RequestHandlerImpl;

/**
 * @author zhanghr
 * @date 2016年9月14日
 */
public class DocHttpHandler implements HttpHandler<Object> {

	private final Map<HandlerKey, TreeMap<String, RequestHandlerImpl>> handlerMap;

	public DocHttpHandler(
			Map<HandlerKey, TreeMap<String, RequestHandlerImpl>> handlerMap) {
		this.handlerMap = handlerMap;
	}

	@Override
	public Object handle(Context context) {
		String path = context.getRequestParameter("path");
		String method = context.getRequestParameter("method");

		return createHTML(path, method);
	}

	private String createHTML(String path, String method) {
		StringBuffer sb = new StringBuffer(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		sb.append("<html><head>");
		sb.append(
				"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
		sb.append("<title>Haetae Api Doc</title>");
		sb.append("<style type=\"text/css\">");
		sb.append("h1 {font-size: 22px;line-height: normal;margin:2 auto;}");
		sb.append("h2 {font-size: 12px;line-height: normal;margin:2 auto;}");
		sb.append(
				"table {border-top: 1px solid #ffffff;border-right: 1px solid #ffffff;font: 12px/1.4 Arial, sans-serif;margin:0 auto;}");
		sb.append(
				"td {background-color: #ece9d8;padding: 2px 5px;text-align: left;border-left: 1px solid #ffffff;border-bottom: 1px solid #ffffff;border-collapse:collapse;}");
		sb.append(
				"td.column {background-color: #0000bb;padding: 2px 4px 2px 4px;color: #ffffff;font-size: 13px;line-height: normal;}");
		sb.append("</style></head>");
		sb.append(
				"<body style=\"margin:auto;width:1024px;text-align:center;\">");
		sb.append("<h1>Haetae Api Doc</h1>");
		sb.append("<table cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("<tr height=\"30\">");
		sb.append("<td class=\"column\">path</td>");
		sb.append("<td class=\"column\">version</td>");
		sb.append("<td class=\"column\">method</td>");
		sb.append("<td class=\"column\">param</td>");
		sb.append("<td class=\"column\">param type</td>");
		sb.append("<td class=\"column\">data type</td>");
		sb.append("<td class=\"column\">required</td>");
		sb.append("<td class=\"column\">description</td>");
		sb.append("<td class=\"column\">testValue</td></tr>");
		if (handlerMap.size() > 0) {
			for (Map.Entry<HandlerKey, TreeMap<String, RequestHandlerImpl>> entry : handlerMap
					.entrySet()) {
				TreeMap<String, RequestHandlerImpl> treeMap = entry.getValue();
				RequestHandlerImpl handler = treeMap.lastEntry().getValue();
				Api api = handler.getApi();
				ApiDoc apiDoc = handler.getApiDoc();
				sb.append("<tr><td>"
						+ (api != null ? api.value()
								: entry.getKey().getRoute().getResourcePath())
						+ "</td>");
				sb.append("<td>"
						+ (api != null ? api.version() : treeMap.lastKey())
						+ "</td>");
				sb.append("<td>" + (api != null ? api.method().value()
						: entry.getKey().getMethod()) + "</td>");
				if (apiDoc != null) {
					sb.append("<td colspan=6>" + apiDoc.name() + "</td></tr>");
					ApiParam[] params = apiDoc.params();
					if (params != null && params.length > 0) {
						for (ApiParam apiParam : params) {
							sb.append("<tr><td colspan=3></td>");
							sb.append("<td>" + apiParam.param() + "</td>");
							sb.append("<td>" + apiParam.type() + "</td>");
							sb.append("<td>" + apiParam.dataType() + "</td>");
							sb.append("<td>" + apiParam.required() + "</td>");
							sb.append("<td>" + apiParam.desc() + "</td>");
							sb.append("<td>" + apiParam.testValue()
									+ "</td></tr>");
						}
					}

				} else
					sb.append("<td colspan=6>no any api doc</td></tr>");
			}
		} else {
			sb.append("<tr><h2>no found api doc</h2></tr>");
		}
		sb.append("</table></body></html>");
		return sb.toString();
	}
}

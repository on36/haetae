package com.on36.haetae.server.core.stats;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

import java.util.ArrayList;
import java.util.List;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.core.HttpHandler;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.http.Container;

public class StatisticsHandler implements HttpHandler<Object> {

	private final Container container;

	public StatisticsHandler(Container container) {
		this.container = container;
	}

	@Override
	public Object handle(Context context) {
		String contentType = context.getHeaderValue(CONTENT_TYPE);
		String path = context.getRequestParameter("path");
		String method = context.getRequestParameter("method");
		List<?> stats = container.getStatistics();
		if (MediaType.APPLICATION_JSON.value().equals(contentType)
				|| MediaType.TEXT_JSON.value().equals(contentType)) {
			return filterStats(stats, path, method);
		}
		return createHTML(stats, path, method);
	}

	@SuppressWarnings("rawtypes")
	private List filterStats(List<?> stats, String path, String method) {
		if (stats.size() > 0) {
			if (path == null)
				return stats;
			List<Statistics> result = new ArrayList<Statistics>();
			for (Object obj : stats) {
				Statistics stat = (Statistics) obj;
				if (stat.getPath().equalsIgnoreCase(path)) {
					if (method == null)
						result.add(stat);
					else if (stat.getMethod().equalsIgnoreCase(method)) {
						if (stat.getChildStatisticsList() != null)
							return stat.getChildStatisticsList();
						else
							result.add(stat);
					}
				}
			}
			return result;
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String createHTML(List<?> stats, String path, String method) {
		StringBuffer sb = new StringBuffer(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		sb.append("<html><head>");
		sb.append(
				"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
		sb.append("<title>Haetae Statistics</title>");
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
		sb.append("<h1>Haetae Performance</h1>");
		sb.append("<table cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("<tr height=\"30\">");
		sb.append("<td class=\"column\">path</td>");
		sb.append("<td class=\"column\">version</td>");
		sb.append("<td class=\"column\">method</td>");
		sb.append("<td class=\"column\">success</td>");
		sb.append("<td class=\"column\">failure</td>");
		sb.append("<td class=\"column\">min elapsed time(ms)</td>");
		sb.append("<td class=\"column\">avg elapsed time(ms)</td>");
		sb.append("<td class=\"column\">max elapsed time(ms)</td>");
		sb.append("<td class=\"column\">current RPS</td>");
		sb.append("<td class=\"column\">max RPS</td></tr>");
		if (stats.size() > 0) {
			List result = filterStats(stats, path, method);
			for (Object obj : result) {
				Statistics stat = (Statistics) obj;
				sb.append("<tr><td>" + stat.getPath() + "</td>");
				sb.append("<td>" + stat.getVersion() + "</td>");
				sb.append("<td>" + stat.getMethod() + "</td>");
				sb.append("<td>" + stat.getSuccessCount() + "</td>");
				sb.append("<td>" + stat.getFailureCount() + "</td>");
				sb.append("<td>" + stat.getMinElapsedTime() + "</td>");
				sb.append("<td>" + stat.getAvgElapsedTime() + "</td>");
				sb.append("<td>" + stat.getMaxElapsedTime() + "</td>");
				sb.append("<td>" + stat.getCurRPS() + "</td>");
				sb.append("<td>" + stat.getMaxRPS() + "</td></tr>");
			}
		} else {
			sb.append("<tr><h2>nothing to find</h2></tr>");
		}
		sb.append("</table></body></html>");
		return sb.toString();
	}

}

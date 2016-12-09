package com.on36.haetae.server.core.manager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.on36.haetae.api.http.Session;
import com.on36.haetae.common.utils.ShortUUID;
import com.on36.haetae.http.request.HttpRequestExt;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

public class SessionManager {

	private static final String SESSION_COOKIE_NAME = "Haetae-Session";

	private final Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	public Session getSessionIfExists(HttpRequestExt request) {

		String value = request.headers().get(HttpHeaderNames.COOKIE);
		if (value == null)
			return null;

		ServerCookieDecoder cookieDecoder = ServerCookieDecoder.STRICT;
		Set<Cookie> cookies = cookieDecoder.decode(value);

		// check invalid session and remove it
		// checkSession();

		for (Cookie cookie : cookies) {
			if (SESSION_COOKIE_NAME.equals(cookie.name())) {
				String sessionId = cookie.value();
				Session session = sessions.get(sessionId);
				if (session != null) {
					if (!session.valid()) {
						sessions.remove(sessionId);
						return null;
					}
				}
				return session;
			}
		}
		return null;
	}

	public Session newSession(HttpResponse response) {

		Session session = new Session();
		String sessionid = ShortUUID.randomUUID();
		sessions.put(sessionid, session);

		Cookie cookie = new DefaultCookie(SESSION_COOKIE_NAME, sessionid);
		ServerCookieEncoder cookieEncoder = ServerCookieEncoder.STRICT;
		response.headers().add(HttpHeaderNames.SET_COOKIE,
				cookieEncoder.encode(cookie));

		return session;
	}

//	private void checkSession() {
//		for (Map.Entry<String, Session> entry : sessions.entrySet()) {
//			Session session = entry.getValue();
//			if (!session.valid()) {
//				sessions.remove(entry.getKey());
//			}
//		}
//	}
}

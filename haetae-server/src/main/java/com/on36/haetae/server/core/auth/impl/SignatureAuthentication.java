package com.on36.haetae.server.core.auth.impl;

import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;

import java.util.Map;

import com.on36.haetae.api.Context;
import com.on36.haetae.server.core.auth.IAuthentication;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class SignatureAuthentication implements IAuthentication {

	private boolean verify = false;

	public void setVerify(boolean value) {
		this.verify = value;
	}

	@Override
	public HttpResponseStatus auth(Context context, HttpResponse response) {
		if (verify) {
			Map<String, String> queryParam = context.getRequestParameters();
			String timestamp = queryParam.get("timestamp");
			String sign = queryParam.get("sign");
			if (sign == null)
				return FORBIDDEN;
			else {
				if (!"abc123".equals(sign))
					return FORBIDDEN;
				else if (timestamp == null)
					return new HttpResponseStatus(418, "Request Expried");
			}
		}
		return null;
	}

}

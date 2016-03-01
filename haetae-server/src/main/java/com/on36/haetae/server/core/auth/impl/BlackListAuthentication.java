package com.on36.haetae.server.core.auth.impl;

import io.netty.handler.codec.http.HttpResponse;

import java.util.Arrays;
import java.util.List;

import com.on36.haetae.http.request.HttpRequestExt;
import com.on36.haetae.server.core.auth.IAuthentication;

public class BlackListAuthentication implements IAuthentication {

	private List<String> blackList;

	public void ban(String... blackips) {

		List<String> newBlack = Arrays.asList(blackips);
		if (blackList == null)
			this.blackList = newBlack;
		else
			this.blackList.addAll(newBlack);
	}

	@Override
	public boolean auth(HttpRequestExt request, HttpResponse response) {
		/* validation black list */
		if (blackList != null) {

			String remoteIp = request.getRemoteAddress().getAddress()
					.getHostAddress();
			if (blackList.contains(remoteIp))
				return false;
		}
		return true;
	}

}

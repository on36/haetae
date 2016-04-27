package com.on36.haetae.api.core;

import com.on36.haetae.api.Context;


public interface HttpRequestHandler<T> {

	 T handle(Context context) throws Exception;
}

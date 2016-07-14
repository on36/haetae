package com.on36.haetae.api.core;

import com.on36.haetae.api.Context;


@Deprecated
public interface HttpHandler<T> {

	 T handle(Context context) throws Exception;
}

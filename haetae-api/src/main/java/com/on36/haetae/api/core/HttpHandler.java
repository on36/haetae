package com.on36.haetae.api.core;

import com.on36.haetae.api.Context;

/**
 * 
 * @author zhanghr
 * @date 2016年1月18日
 * @description 不再推荐使用，请参考'@GET、@POST'用法
 * @see com.on36.haetae.api.annotation.Get
 * @see com.on36.haetae.api.annotation.Post
 */
@Deprecated
public interface HttpHandler<T> {

	 T handle(Context context) throws Exception;
}

package com.on36.haetae.manager;

import java.lang.reflect.Method;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.Get;
import com.on36.haetae.api.annotation.Post;
import com.on36.haetae.api.http.MediaType;
import com.on36.haetae.server.HaetaeServer;

/**
 * @author zhanghr
 * @date 2016年1月3日
 */
public class ServerTest {

	public static void main(String[] args) throws Exception {
		
		int port = 1025;
		if(args != null && args.length == 1)
			port = Integer.parseInt(args[0]);

		HaetaeServer server = new HaetaeServer(port,"/_cluster");

		Class<?> clazz = ClusterManagerService.class;
		Method[] methods = clazz.getDeclaredMethods();
		Object object = null;
		for (Method method : methods) {
			Class<?>[] clazzs = method.getParameterTypes();
			if (clazzs.length == 1
					&& clazzs[0].getName().equals(Context.class.getName())) {

				Post post = method.getAnnotation(Post.class);
				Get get = method.getAnnotation(Get.class);
				if (object == null)
					object = clazz.newInstance();
				if (post != null) {
					if (server.find(post.value()) == null)
						server.register(post,MediaType.TEXT_HTML.value()).with(object, method);
				} else if (get != null) {
					if (server.find(get.value()) == null)
						server.register(get,MediaType.TEXT_HTML.value()).with(object, method);
				}
			}
		}
		server.start();
	}

	class User {
		private String val;
		private String value;

		public String getVal() {
			return val;
		}

		public void setVal(String val) {
			this.val = val;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public User(String val, String value) {
			super();
			this.val = val;
			this.value = value;
		}
	}

}

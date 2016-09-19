
  
为什么会有这个项目？（Haetae）
===================================
         目前，大量微服务框架都基于Servlet容器，严重依赖于WEB服务器，如tomcat,jetty等，并发性能

    也完全依赖WEB容器，在部署应用的时候都会额外增加WEB服务器组件，为了减轻服务框架，做到轻量级部署，所以

    才有本项目的想法，现在还只能是开发和测试版本，请不要用于任何生产环境。

    同时本项目也参考了https://github.com/lantunes/fixd中的部分想法，非常感谢lantunes
  
特性
-----------------------------------
  1，基于netty的Http微服务功能，完全类似RESTFUL服务，所有管理API采用RESTFUL服务提供
  
  2，微服务的服务框架部分只依赖于netty，部署非常方便，轻量化，支持JAR和WAR包发布

  3，提供微服务动态增减和热插拔(目前还没有完成)

  4，提供session功能

  5，提供服务动态增减鉴权功能(白名单，黑名单)

  6，提供动态增减流量控制功能

  7，提供应用超时控制功能

  8，提供API文档描述功能
  
  9，提供性能监控功能
  
TODO LIST
-----------------------------------
  1，微服务动态增减和热插拔
  
  2，在线升级微服务和热部署
  
  3，服务管理(包括服务注册，服务发现，进程发现等)
  
  4，资源管理(包括CPU、内存资源控制)
  
使用指导
-----------------------------------

## 基于基本haetae服务测试

	public class ServerTest {
		public static void main(String[] args) throws Exception {

			int port = 8080;
			if (args != null && args.length == 1)
				port = Integer.parseInt(args[0]);
		
			HaetaeServer server = new HaetaeServer(port, 128);
			server.register("/hello").with("Hello xiongdi!").auth(false);
			server.register("name/:name<[A-Za-z]+>").with("Hello :name");
			server.register("/multi/*/*").with("Hello *[0] *[1]");
			server.register("/greeting").with("Hello [request$User-Agent]");
			server.register("/control").with("Hello control!").every(30,
				TimeUnit.SECONDS, 10);
			server.register("/skip").withRedirect("http://www.baidu.com");
			server.register("/black").with("Hello black!").ban("172.31.25.40",
				"127.0.0.1");
			server.register("/white").with("Hello white!").permit("172.31.25.40",
				"127.0.0.1");
			server.register("/whitecontrol").with("Hello white!")
				.permit("127.0.0.1", ServiceLevel.LEVEL_50);
			server.register("/body", HttpMethod.POST)
				.with(new HttpHandler<String>() {

					public String handle(Context context) {
						return context.getBodyAsString();
					}
				});
			server.register("/custom", HttpMethod.POST)
				.with(new HttpHandler<String>() {

					public String handle(Context context) {
						return context.getRequestParameter("user");
					}
				});
			server.register("/customobject", HttpMethod.POST)
				.with(new HttpHandler<String>() {

					public String handle(Context context) throws Exception {
						// User user = context.getBody(User.class);
						JSONObject jo = context.getBodyAsJSONObject();
						return jo.get("val");
					}
				});
			server.register("/timeout", HttpMethod.GET).timeout(1, TimeUnit.SECONDS)
				.with(new HttpHandler<String>() {

					public String handle(Context context) throws Exception {
						// User user = context.getBody(User.class);
						Thread.sleep(2000);

						System.out.println(Thread.currentThread().getName());
						return context.getURI("/hello");
					}
				});
			server.register("/custombody/*/*", HttpMethod.POST)
				.with(new HttpHandler<String>() {

					public String handle(Context context) throws Exception {
						return context.getCapturedParameter("*[0] *[1]");
					}
				});

			server.start();
	 	}
	}
单元测试

1，[点击这里你可以链接到服务类例子](https://github.com/on36/haetae/blob/master/haetae-test/src/main/java/com/on36/haetae/test/ServerTest.java)<br />
2，[点击这里你可以链接到测试类](https://github.com/on36/haetae/blob/master/haetae-test/src/test/java/com/on36/haetae/test/HaetaeAsynHttpClientTest.java)<br />

## 基于微服务测试

服务端

	public class UserService {

		@Get(value="/user/add",version="1.1")
		public String addUser(Context context) {

			return "lisi";
		}

		@Get("/user/remove/:id")
		public String removeUser(Context context) {

			return context.getCapturedParameter(":id");
		}
		@Get("/user/list/*/*")
		public String list(Context context) {
		
			return context.getCapturedParameter("*[0]");
		}
		@Get("/user/hello")
		public String hello(Context context) {
		
			return "hello";
		}
	}
启动方式
-----------------------------------
方法一，直接依赖启动

	public class ServerTest {

		public static void main(String[] args) throws Exception {

			int port = 8080;
			if (args != null && args.length == 1)
				port = Integer.parseInt(args[0]);

			HaetaeServer server = new HaetaeServer(port, 128);
			Class<?> clazz = UserService.class;
			Method[] methods = clazz.getDeclaredMethods();

			for (Method method : methods) {
				Class<?>[] clazzs = method.getParameterTypes();
				if (clazzs.length == 1
						&& clazzs[0].getName().equals(Context.class.getName())) {

					Post post = method.getAnnotation(Post.class);
					Get get = method.getAnnotation(Get.class);
					if (post != null)
						server.register(post).with(clazz.newInstance(), method);
					else
						server.register(get).with(clazz.newInstance(), method);
				}
			}
			server.start();
		}
	}
或者

	public class ServerTest {

		public static void main(String[] args) throws Exception {

			int port = 8080;
			if (args != null && args.length == 1)
				port = Integer.parseInt(args[0]);
		
			List<String> clazzes = new ArrayList<>();
			clazzes.add("com.on36.haetae.test.UserService");
			HaetaeServer server = new HaetaeServer(port, 128, null, clazzes);
		
			server.start();
		}
	}

方法二 ，从maven仓库下动态启动

 待添加。。

#### 超时响应功能

		HaetaeServer server = new HaetaeServer(8080, 8);
		server.register("/timeout", HttpMethod.GET).timeout(2, TimeUnit.SECONDS)//设置两秒无响应，则请求返回超时
				.with(new HttpHandler<String>() {

					public String handle(Context context) throws Exception {
						// User user = context.getBody(User.class);
						Thread.sleep(5000);

						System.out.println(Thread.currentThread().getName());
						return "hello";
					}
		});
		server.start();
请求地址 http://localhost:8080/services/timeout

返回结果：

	{"status":408,"message":"Request Timeout"}

#### 黑名单功能

		HaetaeServer server = new HaetaeServer(8080, 8);
		server.register("/black").with("Hello black!").ban("172.31.25.40",
				"127.0.0.1");//设置黑名单，禁止IP172.31.25.40和本地127.0.0.1访问/black服务
		server.start();

请求地址 http://localhost:8080/services/black

黑名单返回结果：

	{"status":403,"message":"Forbidden"}

正常返回结果：

	{"status":200,"message":"OK","result":"Hello black!"}

#### 白名单功能

		HaetaeServer server = new HaetaeServer(8080, 8);
		server.register("/white").with("Hello white!").permit("172.31.25.40",
				"127.0.0.1");//设置白名单，只允许IP172.31.25.40和本地127.0.0.1访问/white服务
		server.start();

请求地址 http://localhost:8080/services/white

非白名单返回结果：

	{"status":403,"message":"Forbidden"}

正常返回结果：

	{"status":200,"message":"OK","result":"Hello white!"}

#### 白名单流量设置功能

		HaetaeServer server = new HaetaeServer(8080, 8);
		server.register("/whitecontrol").with("Hello white!")
				.permit("127.0.0.1",10,10,TimeUnit.SECONDS);//设置白名单，限制127.0.0.1地址10秒内只允许10次请求
		server.start();

请求地址 http://localhost:8080/services/whitecontrol

非白名单返回结果：

	{"status":403,"message":"Forbidden"}

正常返回结果：

	{"status":200,"message":"OK","result":"Hello white!"}

流量过多返回结果：

	{"status":429,"message":"Too Many Requests"}

#### 全局流量设置功能

		HaetaeServer server = new HaetaeServer(8080, 8);
		server.register("/control").with("Hello control!").every(30,
				TimeUnit.SECONDS, 10);//限制当前服务10秒内只能有30个请求
		server.start();

请求地址 http://localhost:8080/services/control

正常返回结果：

	{"status":200,"message":"OK","result":"Hello control!"}

流量过多返回结果：

	{"status":429,"message":"Too Many Requests"}

#### URL地址匹配功能

		HaetaeServer server = new HaetaeServer(port, 4);
		server.register("/name/:name").with("Hello :name");
		server.start();

请求地址 http://localhost:8080/services/name/zhangsan

正常返回结果：

	{"status":200,"message":"OK","result":"Hello zhangsan"}

#### URL地址匹配正则限制功能

		HaetaeServer server = new HaetaeServer(port, 4);
		server.register("/name/:name<[A-Za-z]+>").with("Hello :name");
		server.start();

请求地址 http://localhost:8080/services/name/zhangsan

正常返回结果：

	{"status":200,"message":"OK","result":"Hello zhangsan"}

请求地址 http://localhost:8080/services/name/123

错误返回结果：

	{"status":503,"message":"Service Unavailable"}

#### URL地址多参数匹配功能

		HaetaeServer server = new HaetaeServer(port, 4);
		server.register("/multi/*/*").with("Hello *[0] *[1]");
		server.start();

请求地址 http://localhost:8080/services/multi/zhangsan/123

正常返回结果：

	{"status":200,"message":"OK","result":"Hello zhangsan 123"}


#### 自定义响应处理功能(不推荐,这种方式已经放弃)

		HaetaeServer server = new HaetaeServer(port, 4);
		server.register("/custom", HttpMethod.GET)
				.with(new HttpHandler<String>() {

					public String handle(Context context) {
						return "ni hao";
					}
				});
		server.start();		

请求地址 http://localhost:8080/services/custom

正常返回结果：

	{"status":200,"message":"OK","result":"ni hao"}


#### 自定义服务(推荐)

自定义服务类

	public class UserService {
		@Get(value="/user/add",version="1.1")
		public String addUser(Context context) {
			return "lisi";
		}
	｝

启动服务

		HaetaeServer server = new HaetaeServer(port, 4);
		Class<?> clazz = UserService.class;
		Method[] methods = clazz.getDeclaredMethods();

		for (Method method : methods) {
			Class<?>[] clazzs = method.getParameterTypes();
			if (clazzs.length == 1
					&& clazzs[0].getName().equals(Context.class.getName())) {

				Post post = method.getAnnotation(Post.class);
				Get get = method.getAnnotation(Get.class);
				if (post != null)
					server.register(post).with(clazz.newInstance(), method);
				else
					server.register(get).with(clazz.newInstance(), method);
			}
		}
		server.start();		

请求地址 http://localhost:8080/services/user/add

正常返回结果：

	{"status":200,"message":"OK","result":"lisi"}

## 服务API文档描述说明

	public class UserService {
		@Api(value = "/user/:id", method = MethodType.DELETE)
		@ApiDoc(name = "根据用户ID删除当前用户数据", params = {
				@ApiParam(param = "id", type = ParamType.URI, desc = "用户ID", required = true),
				@ApiParam(param = "sign", desc = "数据签名", required = true),
				@ApiParam(param = "timestamp", desc = "时间戳", required = true) })
		public String deleteUser(Context context) {
			String id = context.getCapturedParameter(":id");
			return "delete-" + id;
		}

	｝
启动服务
请求地址 http://localhost:8080/doc
![DOC页面](http://on36.github.io/20160919095402.png)


## 性能监控页面

请求地址 http://localhost:8080/services

![性能页面](http://on36.github.io/20160908150141.png)

## 作者

[点击联系我](mailto:say_hello_plz@qq.com)<br />

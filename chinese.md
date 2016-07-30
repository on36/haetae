
  
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

  8，提供性能监控功能
  
TODO LIST
-----------------------------------
  1，微服务动态增减和热插拔
  
  2，在线升级微服务和热部署
  
  3，服务管理(包括服务注册，服务发现，进程发现等)
  
  4，资源管理(包括CPU、内存资源控制)
  
使用指导
-----------------------------------

### 直接写一个haetae服务测试

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
						context.getURI("/custom");
						return context.getURI("/hello");
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

 待添加。。


### 作者

[点击联系我](mailto:say_hello_plz@qq.com)<br />

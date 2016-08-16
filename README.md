# Haetae
This is a incomplete lightweight micro service framework 

Thanks for https://github.com/lantunes/fixd


USAGE GUIDE
-----------------------------------

### haetae server testing

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
junit test

	public class HaetaeAsynHttpClientTest {

		@Test
		public void testHello() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/hello").execute()
				.get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("Hello xiongdi!", result);
			asyncHttpClient.close();
		}

		@Test
		public void testName() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/name/zhangsan")
				.execute().get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("Hello zhangsan", result);
			asyncHttpClient.close();
		}

		@Test
		public void testMulti() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/multi/zhangsan/123")
				.execute().get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("Hello zhangsan 123", result);
			asyncHttpClient.close();
		}

		@Test
		public void testHeaderValue() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/greeting")
				.execute().get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("Hello NING/1.0", result);
			asyncHttpClient.close();
		}

		@Test
		public void testRequestControl() throws Exception {

			int count = 20;

			while (count-- > 0) {
				AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
				Response resp = asyncHttpClient
					.prepareGet("http://localhost:8080/services/control")
					.execute().get();

				String result = resp.getResponseBody().trim();
				System.out.println(result);
				Assert.assertEquals("Hello control!", result);
				asyncHttpClient.close();
			}
		}

		@Test
		public void testBlackList() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/black").execute()
				.get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("Hello black!", result);
			asyncHttpClient.close();
		}

		@Test
		public void testWhiteList() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.prepareGet("http://localhost:8080/services/white").execute()
				.get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("Hello white!", result);
			asyncHttpClient.close();
		}

		@Test
		public void testWhiteListAndRequestControl() throws Exception {

			int count = 20;

			while (count-- > 0) {
				AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
				Response resp = asyncHttpClient
					.prepareGet("http://localhost:8080/services/whitecontrol")
					.execute().get();

				String result = resp.getResponseBody().trim();
				System.out.println(result);
				Assert.assertEquals("Hello white!", result);
				asyncHttpClient.close();
			}
		}

		@Test
		public void testBodyParts() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/custom")
				.addBodyPart(new StringPart("user", "zhangsan"))
				.addBodyPart(new StringPart("name", "nihao")).execute().get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("nihao", result);
			asyncHttpClient.close();
		}

		@Test
		public void testBodyParameter() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/custom")
				.addQueryParam("user", "zhangsan").addQueryParam("name", "nihao")
				.execute().get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("zhangsan", result);
			asyncHttpClient.close();
		}

		@Test
		public void testSplatParameter() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.preparePost(
						"http://localhost:8080/services/custom?user=zhangsan&name=nihao")
				.execute().get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("zhangsan", result);
			asyncHttpClient.close();
		}

		@Test
		public void testBodyString() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/body")
				.setHeader("Content-Type", "application/json")
				.setBody("{\"val\":\"someJSON\"}").execute().get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("{\"val\":\"someJSON\"}", result);
			asyncHttpClient.close();
		}
		@Test
		public void testBodyObejct() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.preparePost("http://localhost:8080/services/customobject")
				.setHeader("Content-Type", "application/json")
				.setBody("{\"val\":\"someJSON\"}").execute().get();
		
			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("someJSON", result);
			asyncHttpClient.close();
		}

		@Test
		public void testCapturedParameter() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.preparePost(
						"http://localhost:8080/services/custombody/lisi/zhangsan")
				.execute().get();

			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("lisi zhangsan", result);
			asyncHttpClient.close();
		}
		@Test
		public void testTimeout() throws Exception {
			AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
			Response resp = asyncHttpClient
				.prepareGet(
						"http://localhost:8080/services/timeout")
						.execute().get();
		
			String result = resp.getResponseBody().trim();
			System.out.println(result);
			Assert.assertEquals("lisi zhangsan", result);
			asyncHttpClient.close();
		}
	}

### microservice testing

server side

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
HOW TO START 
-----------------------------------
first, use HaetaeServer 

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
or

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

second, running jar being downloaded from maven repository

to be added..

# Author

 [contact me freely](mailto:say_hello_plz@qq.com)<br />

package com.on36.haetae.common.zk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import com.on36.haetae.common.conf.Configuration;
import com.on36.haetae.common.conf.Constant;
import com.on36.haetae.common.log.Logger;
import com.on36.haetae.common.log.LoggerFactory;

/**
 * @author zhanghr
 * @date 2016年4月12日
 */
public abstract class ZKClient implements Watcher {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	protected static final long SLEEP_TIME = 50; // 睡眠毫秒
	protected static final int RETRIES = 10; // 重试次数

	private ZooKeeper zk = null;

	private String connectString = "0.0.0.0:2181"; // 默认地址
	private int sessiontTimeout = 5000;// 默认超时时间
	private String digest = "guest:guest123";// 默认用户

	private List<ACL> acls = new ArrayList<ACL>(2);

	public ZKClient(String connectString, int sessiontTimeout, String digest) {
		this.connectString = connectString;
		this.sessiontTimeout = sessiontTimeout;
		this.digest = digest;
		connect();
	}

	public ZKClient(String connectString, String digest) {
		this(connectString,
				Configuration.create().getInt(
						Constant.K_ZOOKEEPER_SESSION_TIMEOUT,
						Constant.V_ZOOKEEPER_SESSION_TIMEOUT),
				digest);
	}

	public ZKClient(String connectString) {
		this(connectString,
				Configuration.create().getString(
						Constant.K_ZOOKEEPER_AUTH_DIGEST,
						Constant.V_ZOOKEEPER_AUTH_DIGEST));
	}

	/**
	 * 建立连接.
	 * 
	 * @author zhanghr
	 * 
	 */
	private void connect() {
		try {
			connect(connectString, sessiontTimeout);
		} catch (Exception e) {
			LOG.error("zookeeper initial connection failed", e);
		}
	}

	/**
	 * 建立连接.
	 * 
	 * @param connectString
	 *            连接地址
	 * @param sessionTime
	 *            session时间
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	private void connect(String connectString, int sessionTime)
			throws Exception {
		if (zk != null)
			zk.close();

		zk = new ZooKeeper(connectString, sessionTime, this);

		if (digest != null) {
			zk.addAuthInfo("digest", digest.getBytes());
			Id id1 = new Id("digest",
					DigestAuthenticationProvider.generateDigest(digest));
			ACL acl1 = new ACL(ZooDefs.Perms.ALL, id1);
			acls.add(acl1);
		} else
			acls = Ids.OPEN_ACL_UNSAFE;

		zk.register(this);
	}

	/**
	 * 关闭连接.
	 * 
	 * @throws InterruptedException
	 * @author zhanghr
	 * 
	 */
	public void close() throws InterruptedException {
		if (zk != null)
			zk.close();
	}

	/**
	 * 返回当前连接的session id
	 * 
	 * @return
	 */
	public long getSesssionId() {
		return zk.getSessionId();
	}

	/**
	 * 创建一个持久目录,数据值为null.
	 * 
	 * @param path
	 *            目录
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public String create(String path, boolean full) throws Exception {
		byte[] value = null;
		return create(path, value, full);
	}

	/**
	 * 创建一个持久目录.
	 * 
	 * @param path
	 *            目录
	 * @param data
	 *            目录数据String类型
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public String create(String path, String data, boolean full)
			throws Exception {
		byte[] value = null;
		if (data != null)
			value = data.getBytes();
		return create(path, value, full);
	}

	/**
	 * 创建一个持久目录.
	 * 
	 * @param path
	 *            目录
	 * @param data
	 *            目录数据int类型
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public String create(String path, int data, boolean full) throws Exception {
		byte[] value = null;
		value = String.valueOf(data).getBytes();
		return create(path, value, full);
	}

	/**
	 * 创建一个持久目录.
	 * 
	 * @param path
	 *            目录
	 * @param data
	 *            目录数据字节数组
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	private String create(String path, byte[] data, boolean full)
			throws Exception {
		return create(path, data, CreateMode.PERSISTENT, full);
	}

	/**
	 * 创建一个临时目录,数据值为null.
	 * 
	 * @param path
	 *            目录
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public String createEphemeral(String path, boolean full) throws Exception {
		byte[] value = null;
		return createEphemeral(path, value, full);
	}

	/**
	 * 创建一个临时目录.
	 * 
	 * @param path
	 *            目录
	 * @param data
	 *            目录数据String类型
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public String createEphemeral(String path, String data, boolean full)
			throws Exception {
		byte[] value = null;
		if (data != null)
			value = data.getBytes();
		return createEphemeral(path, value, full);
	}

	/**
	 * 创建一个临时目录.
	 * 
	 * @param path
	 *            目录
	 * @param data
	 *            目录数据字节数组
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	private String createEphemeral(String path, byte[] data, boolean full)
			throws Exception {
		return create(path, data, CreateMode.EPHEMERAL, full);
	}

	/**
	 * 创建一个序列目录.
	 * 
	 * @param path
	 *            目录
	 * @param data
	 *            目录数据String类型
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public String createSequential(String path, String data, boolean full)
			throws Exception {
		byte[] value = null;
		if (data != null)
			value = data.getBytes();
		return createSequential(path, value, full);
	}

	/**
	 * 创建一个序列目录.
	 * 
	 * @param path
	 *            目录
	 * @param data
	 *            目录数据字节数组
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	private String createSequential(String path, byte[] data, boolean full)
			throws Exception {
		return create(path, data, CreateMode.PERSISTENT_SEQUENTIAL, full);
	}

	/**
	 * 创建一个临时序列目录.
	 * 
	 * @param path
	 *            目录
	 * @param data
	 *            目录数据String类型
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public String createEphemeralSequential(String path, String data,
			boolean full) throws Exception {
		byte[] value = null;
		if (data != null)
			value = data.getBytes();
		return createEphemeralSequential(path, value, full);
	}

	/**
	 * 创建一个临时序列目录.
	 * 
	 * @param path
	 *            目录
	 * @param data
	 *            目录数据字节数组
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	private String createEphemeralSequential(String path, byte[] data,
			boolean full) throws Exception {
		return create(path, data, CreateMode.EPHEMERAL_SEQUENTIAL, full);
	}

	/**
	 * 创建一个目录.
	 * 
	 * @param path
	 *            目录地址
	 * @param data
	 *            目录数据字节数组
	 * @param createMode
	 *            目录模式
	 * @param full
	 *            是否全目录生成
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public String create(String path, byte[] data, CreateMode createMode,
			boolean full) throws Exception {

		checkConnected();
		boolean running = true;
		int retries = RETRIES;
		while (running) {
			try {
				String p = null;
				if (full) {
					int pos = 1; // skip first slash, root is guaranteed to
									// exist
					do {
						pos = path.indexOf('/', pos + 1);
						if (pos == -1) {
							pos = path.length();
						}
						String subPath = path.substring(0, pos);
						if (pos == path.length()) {
							p = this.zk.create(subPath, data, acls, createMode);
						} else {
							Stat stat = exists(subPath, false);
							if (stat == null) {
								p = this.zk.create(subPath, null, acls,
										CreateMode.PERSISTENT);
							}
						}
					} while (pos < path.length());
				} else {
					p = this.zk.create(path, data, acls, createMode);
				}

				running = false;
				return p;
			} catch (NodeExistsException e) {
				running = false;
				throw new Exception("There is already such node name of ["
						+ path + "], create failed!", e);
			} catch (ConnectionLossException e) {
				running = true;
				reconnect(retries--, e);
			} catch (SessionExpiredException e) {
				running = true;
				reconnect(retries--, e);
			} catch (Exception e) {
				running = false;
				throw e;
			}
		}
		return null;
	}

	/**
	 * 删除当前目录.
	 * 
	 * @param path
	 *            当前目录
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public void delete(String path) throws Exception {
		delete(path, -1);
	}

	/**
	 * 删除当前目录.
	 * 
	 * @param path
	 *            当前目录
	 * @param version
	 *            版本号
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public void delete(String path, int version) throws Exception {

		checkConnected();
		boolean running = true;
		int retries = RETRIES;
		while (running) {
			try {
				List<String> children = getChildren(path, false);
				for (String child : children) {
					String fullPath = builkpath(path, child);
					delete(fullPath, -1);
				}
				this.zk.delete(path, version);
				running = false;
			} catch (ConnectionLossException e) {
				running = true;
				reconnect(retries--, e);
			} catch (SessionExpiredException e) {
				running = true;
				reconnect(retries--, e);
			} catch (NoNodeException e) {
				running = false;
				LOG.info("There is no such node name of [" + path
						+ "], delete failed", e);
			} catch (Exception e) {
				running = false;
				throw e;
			}
		}
	}

	/**
	 * 当前目录是否存在，不watcher当前节点.
	 * 
	 * @param path
	 *            当前目录
	 * @return boolean
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public boolean exists(String path) throws Exception {
		return exists(path, false) != null;
	}

	/**
	 * 当前目录是否存在.
	 * 
	 * @param path
	 *            当前目录
	 * @param watch
	 *            是否watcher
	 * @return Stat
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public Stat exists(String path, boolean watch) throws Exception {

		checkConnected();
		Stat stat = null;
		boolean running = true;
		int retries = RETRIES;
		while (running) {
			try {
				stat = this.zk.exists(path, watch ? this : null);
				running = false;
			} catch (ConnectionLossException e) {
				running = true;
				reconnect(retries--, e);

			} catch (SessionExpiredException e) {
				running = true;
				reconnect(retries--, e);
			} catch (Exception e) {
				running = false;
				throw e;
			}
		}

		return stat;
	}

	/**
	 * 顺序排列指定目录的子节点.
	 * 
	 * @param targetPath
	 *            目标目录
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public List<String> sort(String targetPath) throws Exception {
		List<String> list = getChildren(targetPath);
		Collections.sort(list);
		return list;
	}

	/**
	 * 随机排列指定目录的子节点.
	 * 
	 * @param targetPath
	 *            目标目录
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public List<String> shuffle(String targetPath) throws Exception {
		List<String> list = getChildren(targetPath);
		Collections.shuffle(list);
		return list;
	}

	/**
	 * 获得当前目录的子节点，不watcher当前目录.
	 * 
	 * @param path
	 *            当前目录
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public List<String> getChildren(String path) throws Exception {
		return getChildren(path, false);
	}

	/**
	 * 获得当前目录的子节点.
	 * 
	 * @param path
	 *            当前目录
	 * @param watch
	 *            是否watcher
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public List<String> getChildren(String path, boolean watch)
			throws Exception {

		checkConnected();
		List<String> list = null;
		boolean running = true;
		int retries = RETRIES;
		while (running) {
			try {
				list = this.zk.getChildren(path, watch ? this : null);
				running = false;
			} catch (ConnectionLossException e) {
				running = true;
				reconnect(retries--, e);
			} catch (SessionExpiredException e) {
				running = true;
				reconnect(retries--, e);
			} catch (NoNodeException e) {
				running = false;
				throw new Exception("There is no such node name of [" + path
						+ "], getChildren failed", e);
			} catch (Exception e) {
				running = false;
				throw e;
			}
		}
		return list;
	}

	/**
	 * 设置当前目录的数据.
	 * 
	 * @param path
	 *            当前目录
	 * @param data
	 *            数据String类型
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public Stat setData(String path, String data) throws Exception {
		byte[] value = null;
		if (data != null)
			value = data.getBytes();

		return setData(path, value);
	}

	/**
	 * 设置当前目录的数据.
	 * 
	 * @param path
	 *            当前目录
	 * @param data
	 *            数据String类型
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public Stat setData(String path, int data) throws Exception {
		byte[] value = null;
		if (data > -1)
			value = String.valueOf(data).getBytes();

		return setData(path, value);
	}

	/**
	 * 设置当前目录的数据.
	 * 
	 * @param path
	 *            当前目录
	 * @param data
	 *            数据字节数组
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	private Stat setData(String path, byte[] data) throws Exception {

		checkConnected();
		boolean running = true;
		int retries = RETRIES;
		Stat stat = null;
		while (running) {
			try {
				stat = this.zk.setData(path, data, -1);
				running = false;
			} catch (ConnectionLossException e) {
				running = true;
				reconnect(retries--, e);
			} catch (SessionExpiredException e) {
				running = true;
				reconnect(retries--, e);
			} catch (NoNodeException e) {
				running = false;
				throw new Exception("There is no such node name of [" + path
						+ "], setData failed", e);
			} catch (Exception e) {
				running = false;
				throw e;
			}
		}
		return stat;
	}

	/**
	 * 获得当前目录的数据并转换成String类型,且不watcher当前目录.
	 * 
	 * @param path
	 *            目录地址
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public String getData2String(String path) throws Exception {
		return getData2String(path, false);
	}

	/**
	 * 获得当前目录的数据并转换成String类型.
	 * 
	 * @param path
	 *            目录地址
	 * @param watch
	 *            是否watcher
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public String getData2String(String path, boolean watch) throws Exception {
		byte[] data = getData(path, watch);
		return data == null ? null : new String(data);
	}

	/**
	 * 获得当前目录的数据并转换成Boolean类型,且不watcher当前目录.
	 * 
	 * @param path
	 *            目录地址
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public Boolean getData2Boolean(String path) throws Exception {
		return getData2Boolean(path, false);
	}

	/**
	 * 获得当前目录的数据并转换成Boolean类型.
	 * 
	 * @param path
	 *            目录地址
	 * @param watch
	 *            是否watcher
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public Boolean getData2Boolean(String path, boolean watch)
			throws Exception {
		String data = getData2String(path, watch);
		return data == null ? null : Boolean.valueOf(data);
	}

	/**
	 * 获得当前目录的数据并转换成Integer类型,且不watcher当前目录.
	 * 
	 * @param path
	 *            目录地址
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public Integer getData2Integer(String path) throws Exception {
		return getData2Integer(path, false);
	}

	/**
	 * 获得当前目录的数据并转换成Integer类型.
	 * 
	 * @param path
	 *            目录地址
	 * @param watch
	 *            是否watcher
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public Integer getData2Integer(String path, boolean watch)
			throws Exception {
		String data = getData2String(path, watch);
		return data == null ? null : Integer.parseInt(data);
	}

	/**
	 * 获得当前目录的数据,且不watcher当前目录.
	 * 
	 * @param path
	 *            目录地址
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public byte[] getData(String path) throws Exception {
		return getData(path, false);
	}

	/**
	 * 获得当前目录的数据.
	 * 
	 * @param path
	 *            目录地址
	 * @param watch
	 *            是否watcher
	 * @return
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	public byte[] getData(String path, boolean watch) throws Exception {

		checkConnected();
		byte[] data = null;
		boolean running = true;
		int retries = RETRIES;
		while (running) {
			try {
				data = this.zk.getData(path, watch ? this : null, null);
				running = false;
			} catch (ConnectionLossException e) {
				running = true;
				reconnect(retries--, e);
			} catch (SessionExpiredException e) {
				running = true;
				reconnect(retries--, e);
			} catch (NoNodeException e) {
				running = false;
				throw new Exception("There is no such node name of [" + path
						+ "], getData failed", e);
			} catch (Exception e) {
				running = false;
				throw e;
			}
		}
		return data;
	}

	/**
	 * 构建路径.
	 * 
	 * @param parent
	 * @param child
	 * @return
	 * @author zhanghr
	 * 
	 */
	private String builkpath(String parent, String child) {
		boolean running = true;
		while (running) {
			StringBuilder path = new StringBuilder();

			if (!parent.startsWith("/")) {
				path.append("/");
			}
			path.append(parent);
			if ((child == null) || (child.length() == 0)) {
				return path.toString();
			}

			if (!parent.endsWith("/")) {
				path.append("/");
			}

			if (child.startsWith("/")) {
				path.append(child.substring(1));
			} else {
				path.append(child);
			}
			running = false;
			return path.toString();
		}
		return null;
	}

	/**
	 * 当前是否已连接.
	 * 
	 * @return
	 * @author zhanghr
	 * 
	 */
	public boolean isConnected() {
		if (zk == null) {
			return false;
		}
		States state = zk.getState();
		if (state.isConnected()) {
			return true;
		}
		return false;
	}

	private void checkConnected() throws Exception {
		if (!isConnected()) {
			int i = 3;
			while (i-- > 0) {
				Thread.sleep(50);
				if (isConnected())
					return;
			}
			throw new Exception("no connect to server[" + connectString + "]");
		}
	}

	/**
	 * 重连.
	 * 
	 * @param retry
	 *            重连次数
	 * @param e
	 *            异常
	 * @throws Exception
	 * @author zhanghr
	 * 
	 */
	private void reconnect(int retry, Exception e) throws Exception {
		if (retry == 0) {
			LOG.error("Conld not connect to zookeeper server[" + connectString
					+ "]", e);
			throw new Exception("Conld not connect to zookeeper server["
					+ connectString + "]", e);
		}
		LOG.info("Retrying connect to zookeeper server[" + connectString
				+ "]. Already tried " + (RETRIES - retry) + " time(s)");
		try {
			Thread.sleep(SLEEP_TIME);
			connect();
		} catch (InterruptedException e2) {
			Thread.currentThread().interrupt();
		}
	}
}

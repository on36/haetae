package com.on36.haetae.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.on36.haetae.common.log.Logger;
import com.on36.haetae.common.log.LoggerFactory;

/**
 * @author zhanghr
 * @date 2016年1月12日
 */
public class NetworkUtils {
	protected static final Logger LOG = LoggerFactory
			.getLogger(NetworkUtils.class);
	private static String innerIP = null;
	private static String outerIP = null;
	private static NetworkInterface innerNI = null;
	private static NetworkInterface outerNI = null;

	/**
	 * 获得当前机器的一个内网IP地址.
	 * 
	 * @return
	 * @author zhanghr
	 */
	public static String getInnerIP() {
		if (innerIP == null)
			localHostAddress();

		return innerIP;
	}

	/**
	 * 获得当前机器的一个外网IP地址.
	 * 
	 * @return
	 * @author zhanghr
	 */
	public static String getOuterIP() {
		if (outerIP == null)
			localHostAddress();

		return outerIP;
	}

	/**
	 * 获得当前机器的一个本地网络接口.
	 * 
	 * @return
	 * @author zhanghr
	 */
	public static NetworkInterface getLocalNetworkInterface() {
		if (innerNI == null)
			localHostAddress();

		return innerNI;
	}

	/**
	 * 获得当前机器的一个外网网络接口.
	 * 
	 * @return
	 * @author zhanghr
	 */
	public static NetworkInterface getOuterNetworkInterface() {
		if (outerNI == null)
			localHostAddress();

		return outerNI;
	}

	/**
	 * 解析当前机器的一个网络地址.
	 * 
	 * @return
	 * @author zhanghr
	 */
	private static void localHostAddress() {
		try {
			// Iterate all NICs (network interface cards)...
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface
					.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces
						.nextElement();
				// Iterate all IP addresses assigned to each card...
				for (Enumeration<InetAddress> inetAddrs = iface
						.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs
							.nextElement();
					if (!inetAddr.isLoopbackAddress()) {
						if (!inetAddr.isSiteLocalAddress() && inetAddr
								.getHostAddress().indexOf(":") == -1) {// 外网IP
							outerIP = inetAddr.getHostAddress();
							outerNI = iface;
							if (LOG.isDebugEnabled())
								LOG.debug("catch outer ip = " + outerIP
										+ " and outer network interfce mac address "
										+ getMacAddress(iface));
						} else if (inetAddr.isSiteLocalAddress() && inetAddr
								.getHostAddress().indexOf(":") == -1) {// 内网IP
							if (innerIP == null) {
								innerIP = inetAddr.getHostAddress();
								innerNI = iface;
								if (LOG.isDebugEnabled())
									LOG.debug("catch inner ip = " + innerIP
											+ " and inner network interfce mac address "
											+ getMacAddress(iface));
							}
						}
					}
				}
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost()
			// returns...
			if (innerIP == null) {
				InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
				if (jdkSuppliedAddress == null) {
					throw new UnknownHostException(
							"The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
				} else {
					innerIP = jdkSuppliedAddress.getHostAddress();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getMacAddress(NetworkInterface iface)
			throws Exception {
		// 结果是一个byte数组，每项是一个byte，我们需要通过parseByte方法转换成常见的十六进制表示
		byte[] addres = iface.getHardwareAddress();
		if (addres != null) {
			// 获得MAC地址
			StringBuffer sb = new StringBuffer();
			if (addres != null && addres.length > 1) {
				sb.append(parseByte(addres[0])).append(":")
						.append(parseByte(addres[1])).append(":")
						.append(parseByte(addres[2])).append(":")
						.append(parseByte(addres[3])).append(":")
						.append(parseByte(addres[4])).append(":")
						.append(parseByte(addres[5]));
				return sb.toString();
			}
		}
		return null;
	}

	// 格式化二进制
	private static String parseByte(byte b) {
		int intValue = 0;
		if (b >= 0) {
			intValue = b;
		} else {
			intValue = 256 + b;
		}
		return Integer.toHexString(intValue);
	}

	public static void main(String[] args) {
		System.out.println(NetworkUtils.getInnerIP());
	}
}

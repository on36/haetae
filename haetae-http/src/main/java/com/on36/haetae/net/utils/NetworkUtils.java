package com.on36.haetae.net.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author zhanghr
 * @date 2016年1月12日
 */
public class NetworkUtils {

	private static String ip = null;
	private static NetworkInterface ni = null;

	/**
	 * 获得当前机器的一个本地IP地址.
	 * 
	 * @return
	 * @author zhanghr
	 */
	public static String getLocalIP() {
		if (ip == null)
			localHostAddress();

		return ip;
	}

	/**
	 * 获得当前机器的一个本地网络接口.
	 * 
	 * @return
	 * @author zhanghr
	 */
	public static NetworkInterface getLocalNetworkInterface() {
		if (ni == null)
			localHostAddress();

		return ni;
	}

	/**
	 * 获得当前机器的一个网络IP地址.
	 * 
	 * @return
	 * @author zhanghr
	 */
	private static InetAddress localHostAddress() {
		try {
			InetAddress candidateAddress = null;
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

						if (inetAddr.isSiteLocalAddress()) {
							ip = inetAddr.getHostAddress();
							ni = iface;
							// Found non-loopback site-local address. Return it
							// immediately...
							return inetAddr;
						} else if (candidateAddress == null) {
							// Found non-loopback address, but not necessarily
							// site-local.
							// Store it as a candidate to be returned if
							// site-local address is not subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback
							// non-site-local addresses as candidates,
							// only the first. For subsequent iterations,
							// candidate will be non-null.
						}
					}
				}
			}
			if (candidateAddress != null) {
				// We did not find a site-local address, but we found some other
				// non-loopback address.
				// Server might have a non-site-local address assigned to its
				// NIC (or it might be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
				return candidateAddress;
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost()
			// returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException(
						"The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

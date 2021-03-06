package com.on36.haetae.common.utils;

import java.math.BigInteger;
import java.util.UUID;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public final class ShortUUID {

	public static String randomUUID() {
		return Builder.randomUUID();
	}

	private static class Builder {
		private static char[] alphabet = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
				.toCharArray();
		private static int alphabetSize = alphabet.length;

		private static String randomUUID() {
			return build(UUID.randomUUID());
		}

		private static String build(UUID uuid) {
			String uuidStr = uuid.toString().replaceAll("-", "");

			Double factor = Math.log(25d) / Math.log(alphabetSize);
			Double length = Math.ceil(factor * 16);

			BigInteger number = new BigInteger(uuidStr, 16);
			String encoded = encode(number, alphabet, length.intValue());

			return encoded;
		}

//		private static String decode(String ShortUUID) {
//			return decode(ShortUUID.toCharArray(), alphabet);
//		}

		private static String encode(final BigInteger bigInt,
				final char[] alphabet, final int padToLen) {
			BigInteger value = new BigInteger(bigInt.toString());
			BigInteger alphaSize = BigInteger.valueOf(alphabetSize);
			StringBuilder ShortUUID = new StringBuilder();

			while (value.compareTo(BigInteger.ZERO) > 0) {
				BigInteger[] fracAndRemainder = value
						.divideAndRemainder(alphaSize);
				ShortUUID.append(alphabet[fracAndRemainder[1].intValue()]);
				value = fracAndRemainder[0];
			}

			if (padToLen > 0) {
				int padding = Math.max(padToLen - ShortUUID.length(), 0);
				for (int i = 0; i < padding; i++)
					ShortUUID.append(alphabet[0]);
			}

			return ShortUUID.toString();
		}

//		private static String decode(final char[] encoded,
//				final char[] alphabet) {
//			BigInteger sum = BigInteger.ZERO;
//			BigInteger alphaSize = BigInteger.valueOf(alphabetSize);
//			int charLen = encoded.length;
//
//			for (int i = 0; i < charLen; i++) {
//				sum = sum.add(alphaSize.pow(i).multiply(BigInteger
//						.valueOf(Arrays.binarySearch(alphabet, encoded[i]))));
//			}
//
//			String str = sum.toString(16);
//
//			// Pad the most significant bit (MSG) with 0 (zero) if the string is
//			// too short.
//			if (str.length() < 32) {
//				str = String.format("%32s", str).replace(' ', '0');
//			}
//
//			StringBuilder sb = new StringBuilder().append(str.substring(0, 8))
//					.append("-").append(str.substring(8, 12)).append("-")
//					.append(str.substring(12, 16)).append("-")
//					.append(str.substring(16, 20)).append("-")
//					.append(str.substring(20, 32));
//
//			return sb.toString();
//		}
	}
}

package com.on36.haetae.net.udp;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class Message {

	private static final int PACKET_MAX_LENGTH = Integer.parseInt(System
			.getProperty("maxLength", "4096"));

	public enum Title {
		TEST(0), ENDPOINT(1), SESSSION(2), MAVEN(3);

		private final int value;

		private Title(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private final Title title;
	private final byte[] content;
	
	private Message(Title title, byte[] content) {
		this.title = title;
		this.content = content;

		if (content.length > PACKET_MAX_LENGTH - 4) {
			throw new IllegalArgumentException(
					"the length of message is too large !");
		}
	}

	public static Message newMessage(Title title, byte[] content) {
		return new Message(title, content);
	}

	public Title title() {
		return title;
	}

	public byte[] content() {
		return content;
	}

	public static Message toMessage(byte[] msg) throws Exception {
		int title = msg[0];
		return newMessage(Title.values()[title],
				Arrays.copyOfRange(msg, 1, msg.length));
	}

	public byte[] toBytes() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(PACKET_MAX_LENGTH);
		buffer.putInt(title().getValue());
		buffer.put(content());
		return buffer.array();
	}
}

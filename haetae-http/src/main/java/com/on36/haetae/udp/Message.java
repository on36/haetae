package com.on36.haetae.udp;

/**
 * @author zhanghr
 * @date 2016年1月8日
 */
public class Message {

	public enum Title {
		TEST("TEST"), ENDPOINT("ENDPOINT"), SESSSION("SESSION");

		private final String value;

		private Title(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private final Title title;
	private final String content;

	public Message(Title title, String content) {
		super();
		this.title = title;
		this.content = content;
	}

	public Title title() {
		return title;
	}

	public String content() {
		return content;
	}

	public static Message toMessage(byte[] msg, String charsetName)
			throws Exception {
		String message = new String(msg, charsetName);
		int index = message.indexOf(":");
		return new Message(Title.valueOf(message.substring(0, index)),
				message.substring(index + 1));
	}

	public byte[] toBytes(String charsetName) throws Exception {
		String msg = title.getValue() + ":" + content();
		return msg.getBytes(charsetName);
	}
}

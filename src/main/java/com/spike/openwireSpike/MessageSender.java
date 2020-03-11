package com.spike.openwireSpike;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

public class MessageSender {

	private Destination destination;

	private long sleepTime;

	private int messageSize = 255;
	private static int parallelThreads = 1;
	private long timeToLive;
	private String user = ActiveMQConnection.DEFAULT_USER;
	private String password = ActiveMQConnection.DEFAULT_PASSWORD;
	private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private String subject = "TOOL.DEFAULT";
	private boolean topic;
	private boolean transacted;
	private boolean persistent;

	public void send() {

		Connection connection = null;
		PooledConnectionFactory pooledConnectionFactory;
		try {
			// Create the connection.
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
			pooledConnectionFactory = new PooledConnectionFactory(connectionFactory);

			connection = pooledConnectionFactory.createConnection();
			connection.start();

			// Create the session
			Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);

			// topic is set to false for now
			topic = false;

			if (topic) {
				destination = session.createTopic(subject);
			} else {
				destination = session.createQueue(subject);
			}

			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);

			// Text message example:

			TextMessage message = session.createTextMessage(createMessageText(1));

			String msg = message.getText();
			if (msg.length() > 50) {
				msg = msg.substring(0, 50) + "...";
			}
			System.out.println("[" + this + "] Sending message: '" + msg + "'");

			producer.send(message);

			session.commit();

		} catch (Exception e) {

		} finally {
			try {
				connection.close();
			} catch (Throwable ignore) {
			}
		}
	}

	private String createMessageText(int index) {
		StringBuffer buffer = new StringBuffer(messageSize);
		buffer.append("Message: " + index + " sent at: " + new Date());
		if (buffer.length() > messageSize) {
			return buffer.substring(0, messageSize);
		}
		for (int i = buffer.length(); i < messageSize; i++) {
			buffer.append(' ');
		}
		return buffer.toString();
	}

	public static void main(String[] args) {

		System.out.println("Testing Message Sender for Rupa");

		
		com.spike.openwireSpike.MessageSender sender = new 
				com.spike.openwireSpike.MessageSender();
				sender.send();

		System.out.println("Hello 3");
	}

	public void showParameters() {
		System.out.println("Connecting to URL: " + url + " (" + user + ":" + password + ")");
		System.out.println("Publishing a Message with size " + messageSize + " to " + (topic ? "topic" : "queue") + ": "
				+ subject);
		System.out.println("Using " + (persistent ? "persistent" : "non-persistent") + " messages");
		System.out.println("Sleeping between publish " + sleepTime + " ms");
		System.out.println("Running " + parallelThreads + " parallel threads");

		if (timeToLive != 0) {
			System.out.println("Messages time to live " + timeToLive + " ms");
		}
	}
}

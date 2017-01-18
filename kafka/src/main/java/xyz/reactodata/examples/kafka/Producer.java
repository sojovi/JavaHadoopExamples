package xyz.reactodata.examples.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Producer extends Thread {
	private final KafkaProducer<Integer, String> producer;
	private final String topic;

	public Producer(String topic) {
		Properties props = new Properties();
		props.put("bootstrap.servers", KafkaExampleProperties.KAFKA_SERVER_URL + ":" + KafkaExampleProperties.KAFKA_SERVER_PORT);
		props.put("client.id", "ExampleProducer");
		props.put("key.serializer",
				"org.apache.kafka.common.serialization.IntegerSerializer");
		props.put("value.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<>(props);
		this.topic = topic;
	}

	public void run() {
		int messageNo = 1;
		while (messageNo<10) {
			String messageStr = "Message_" + messageNo;
			try {
				producer.send(
						new ProducerRecord<>(topic, messageNo, messageStr))
						.get();
				System.out.println("Sent message: (" + messageNo + ", "
						+ messageStr + ")");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			++messageNo;
		}
	}
}

package org.hadoop.examples.kafka;

public class KafkaExampleProperties {
	public static final String TOPIC = "topic2";
	public static final String KAFKA_SERVER_URL = "quickstart.cloudera";
	public static final int KAFKA_SERVER_PORT = 9092;
	public static final int KAFKA_PRODUCER_BUFFER_SIZE = 64 * 1024;
	public static final int CONNECTION_TIMEOUT = 100000;

	private KafkaExampleProperties() {
	}
}

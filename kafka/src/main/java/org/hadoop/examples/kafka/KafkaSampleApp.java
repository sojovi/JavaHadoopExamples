package org.hadoop.examples.kafka;

public class KafkaSampleApp {

	/**
	 * Start producer and wait for generate some data
	 * 
	 * Note:
	 * Before run this example issue the following command on kafka cluster:
	 * kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topic2
	 * Where topic name should be from KafkaExampleProperties.TOPIC variable
	 */
	private static void generateData() {
		Producer producer = new Producer(KafkaExampleProperties.TOPIC);
		producer.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		generateData();
		Consumer consumer = new Consumer(KafkaExampleProperties.TOPIC);
		consumer.start();
	}
}
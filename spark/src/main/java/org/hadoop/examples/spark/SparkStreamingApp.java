package org.hadoop.examples.spark;

import java.util.Arrays;

import org.apache.spark.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;

import scala.Tuple2;

/**
 * 
 * 1. Start the netcat in console with command "nc -lk 9999"
 * 2. Start the Spark Streaming app
 * 3. Put some words in the console from step 1
 * 4. Read the output of spark
 */

public class SparkStreamingApp {

	@SuppressWarnings({ "resource", "serial" })
	public static void main(String[] args) {
		// Create a StreamingContext with two working thread and batch interval of 1 second
		SparkConf conf = new SparkConf().setMaster("yarn-client").setAppName("NetworkWordCount");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
		
		// Create a DStream that will connect to hostname:port, like localhost:9999
		JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 9999);
		
		// Split each line into words
		JavaDStream<String> words = lines.flatMap(
		  new FlatMapFunction<String, String>() {
		    @Override public Iterable<String> call(String x) {
		      return Arrays.asList(x.split(" "));
		    }
		  });
		
		// Count each word in each batch
		JavaPairDStream<String, Integer> pairs = words.mapToPair(
		  new PairFunction<String, String, Integer>() {
		    @Override public Tuple2<String, Integer> call(String s) {
		      return new Tuple2<String, Integer>(s, 1);
		    }
		  });
		JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
		  new Function2<Integer, Integer, Integer>() {
			@Override public Integer call(Integer i1, Integer i2) {
		      return i1 + i2;
		    }
		  });

		// Print the first ten elements of each RDD generated in this DStream to the console
		wordCounts.print();
		
		jssc.start();              // Start the computation
		jssc.awaitTermination();   // Wait for the computation to terminate
	}
}

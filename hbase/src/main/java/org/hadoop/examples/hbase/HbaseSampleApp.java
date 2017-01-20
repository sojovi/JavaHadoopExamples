package org.hadoop.examples.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * This app create a table in hbase, inserts new record with key/value increment and prints the output.
 *
 */
public class HbaseSampleApp {
	public static void main(String[] args) throws IOException {
		final String table_name = "sample_table";

		// Config use core-site/hdfs-site files from classpath
		Configuration config = HBaseConfiguration.create();
		Connection connection = ConnectionFactory.createConnection(config);

		// Administration tasks
		Admin admin = connection.getAdmin();
		if (!admin.isTableAvailable(TableName.valueOf(table_name))) {
			System.out.println("Creating table.. ");
			HTableDescriptor desc = new HTableDescriptor(
					TableName.valueOf(table_name));
			HColumnDescriptor acc = new HColumnDescriptor("acc".getBytes());

			desc.addFamily(acc);
			admin.createTable(desc);
		}
		admin.close();

		// read/write tasks
		Table table = connection.getTable(TableName.valueOf(table_name));
		try {
			byte[] column_family = Bytes.toBytes("acc");
			byte[] column_name = Bytes.toBytes("amount");
			
			Scan scan = new Scan();
			scan.addColumn(column_family, column_name);
			ResultScanner rs = table.getScanner(scan);
			String value = null;
			String key = null;
			System.out.println("List existing rows.. ");
			for (Result r = rs.next(); r != null; r = rs.next()) {
				byte[] valueObj = r.getValue(column_family, column_name);
				byte[] rowKey = r.getRow();
				
				value = Bytes.toString(valueObj);
				key = Bytes.toString(rowKey);
				System.out.println("key: " + key + "; Value: "+ value);
			}
			int newKey = Integer.parseInt(key)+1;
			int newValue = Integer.parseInt(value)+10;
			Put newRow = new Put(Bytes.toBytes(String.valueOf(newKey)));
			newRow.addColumn(column_family, column_name, Bytes.toBytes(String.valueOf(newValue)));
			System.out.println("Adding new row - key: " + newKey + "; Value: "+ newValue);
			table.put(newRow);
		} finally {
			table.close();
			connection.close();
		}
	}
}

package org.hadoop.examples.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;


/**
 * 
 * Check the http://quickstart.cloudera:8983 url to see the SOLR web ui. 
 * I will use pre-existing cloudera example data, but is also possible to create a new core
 * 
 * Useful links: http://stackoverflow.com/questions/35603902/how-to-start-using-apache-solr-with-java-and-run-simple-queries-without-running
 * https://cwiki.apache.org/confluence/display/solr/Using+SolrJ
 * http://lucene.apache.org/solr/6_3_0/quickstart.html
 * https://cwiki.apache.org/confluence/display/solr/Searching
 *
 */
public class SolrSampleApp {
	public static HttpSolrServer server;

	public static void main(String[] args) throws SolrServerException, IOException {
		SolrSampleApp solr = new SolrSampleApp();
		//solr.addDoc();
		solr.printSchema();
		solr.selectUsers();
	}

	public SolrSampleApp() {
		// is recommended to keep server instance static and avoid of
		// initialization of new instances
		server = new HttpSolrServer("http://quickstart.cloudera:8983/solr/twitter_demo_shard1_replica1");
		server.setMaxRetries(1); // defaults to 0. > 1 not recommended.
		server.setConnectionTimeout(5000); // 5 seconds to establish TCP
	}

	public void addDoc() throws SolrServerException, IOException {
		SolrInputDocument doc1 = new SolrInputDocument();
		doc1.addField("id", 123, 1.0f);
		doc1.addField("user_name", "test_name", 1.0f);

		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		docs.add(doc1);
		server.add(docs);
		server.commit();
	}
	
	public void printSchema() throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setRequestHandler("/schema/fields");
		query.setParam("showDefaults", true);
		QueryResponse response = server.query(query);
		System.out.println(response.getResponse());
	}
	
	public void selectUsers() throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setFields("user_name", "text"); //set fields to retrieve
		query.setQuery("user_location:Malaysia OR user_location:London"); //set search conditions
		QueryResponse response = server.query(query);
		System.out.println(response.getResults());
	}
}

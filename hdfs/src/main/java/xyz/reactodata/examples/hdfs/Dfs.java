package xyz.reactodata.examples.hdfs;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.security.UserGroupInformation;

import java.security.PrivilegedExceptionAction;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Implementation of the basic commands to access the HDFS Includes @Dfs.copy
 * and @Dfs.put. Technically, copy method can do both jobs, but here i added
 * another method just to show another way of interacting with hdfs
 */
public class Dfs {

	private final static String COPY_CMD = "copy";
	private final static String PUT_CMD = "put";

	public static void main(String[] args) throws InterruptedException {

		ArgumentParser parser = ArgumentParsers.newArgumentParser("Dfs")
				.defaultHelp(true).description("Run basic HDFS commands.");
		parser.addArgument("command").type(String.class).required(true)
				.choices(COPY_CMD, PUT_CMD).help("Command to run");
		parser.addArgument("src").type(String.class).help("Source file path");
		parser.addArgument("dst").type(String.class)
				.help("Destination file path");
		parser.addArgument("--user")
				.type(String.class)
				.required(false)
				.setDefault("cloudera")
				.help("Impersonation user. Required if you're trying to access remote cluster");

		try {
			Namespace res = parser.parseArgs(args);
			String srcFilePath = res.get("src");
			String dstFilePath = res.get("dst");

			if (res.get("command").equals(COPY_CMD)) {
				Dfs.copy(srcFilePath, dstFilePath, res.getString("user"));
			} else if (res.get("command").equals(PUT_CMD)) {
				Dfs.put(srcFilePath, dstFilePath);
			}
		} catch (IOException e) {
			System.out.println(ExceptionUtils.getStackTrace(e));
		} catch (ArgumentParserException e) {
			parser.handleError(e);
		}
	}

	/**
	 * 
	 * @param srcPath
	 *            f.e.
	 *            "file:///media/sf_JavaHadoopExamples/JavaExamplesRoot/pom.xml"
	 * @param dstPath
	 *            f.e. "/user/cloudera/pom.xml"
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void copy(final String srcPath, final String dstPath,
			final String asUser) throws IOException, InterruptedException {

		/**
		 * UserGroupInformation object required to impersonate the user on the
		 * remote cluster. It's not needed if the program is executed locally
		 * from the cluster node
		 */
		UserGroupInformation ugi = UserGroupInformation
				.createRemoteUser(asUser);

		ugi.doAs(new PrivilegedExceptionAction<Void>() {

			public Void run() throws Exception {

				Configuration conf = new Configuration();
				conf.set("hadoop.job.ugi", asUser);

				Path inputPath = new Path(srcPath);
				FileSystem inputFS = inputPath.getFileSystem(conf); // local or
																	// remote FS

				Path outputPath = new Path(dstPath);
				FileSystem outputFS = FileSystem.get(conf); // remote FS
				boolean res = FileUtil.copy(inputFS, inputPath, outputFS,
						outputPath, false, conf);

				if (res) {
					System.out.println("Success");
				}
				inputFS.close();
				outputFS.close();
				return null;
			}
		});

	}

	public static void put(String srcLocalPath, String dstPath)
			throws IOException {
		Configuration conf = new Configuration();

		Path srcPath = new Path(srcLocalPath);
		FileSystem fs = srcPath.getFileSystem(conf);
		FSDataInputStream inputStream = fs.open(srcPath);
		Path outputPath = new Path(dstPath);
		FileSystem outputFS = FileSystem.get(conf); // remote FS
		FSDataOutputStream outputStream = outputFS.create(outputPath);

		IOUtils.copy(inputStream, outputStream);
		inputStream.close();
		outputStream.close();
		System.out.println("Success");
		fs.close();
		outputFS.close();
	}
}

package com.spotify.cassandra.course;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

	private final Keyspace keyspace;

	public Main() {

		AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
				.forCluster("JavaJava")
				.forKeyspace("JavaKeyspaceName")
				.withAstyanaxConfiguration(
						new AstyanaxConfigurationImpl()
								.setDiscoveryType(NodeDiscoveryType.NONE))
				.withConnectionPoolConfiguration(
						new ConnectionPoolConfigurationImpl("MyConnectionPool")
								.setPort(9160).setMaxConnsPerHost(1)
								.setSeeds("127.0.0.1:9160"))
				.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
				.buildKeyspace(ThriftFamilyFactory.getInstance());

		context.start();
		this.keyspace = context.getEntity();

	}

	public static final ColumnFamily<String, Long> CF_METRIC = new ColumnFamily<String, Long>(
			"Metric", // Column Family Name
			StringSerializer.get(), // Key Serializer
			LongSerializer.get()); // Column Serializer

	public static final long MS_DAY = 86400000;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		printUsage();

		System.out.println(buildRowKey(System.currentTimeMillis(), "apa"));

        Main myMain = new Main();
        for (int i = 0; i < 100; i++) {
            Long time = System.currentTimeMillis();
            myMain.writeMetric("load", time, Metric.cpuLoad());
            System.out.println(Metric.cpuLoad());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException("Got interrupted");
            }
        }

	}

	public void writeMetric(String metric, final Long ts, Double data) {

		String rowKey = buildRowKey(ts, metric);

		MutationBatch m = keyspace.prepareMutationBatch();

		m.withRow(CF_METRIC, rowKey).putColumn(ts, data, null);

		try {
			OperationResult<Void> result = m.execute();
		} catch (ConnectionException e) {
		}
	}

	public static String buildRowKey(final long ts, String metric) {

		long now = ts;

		long dayMs = now - (now % MS_DAY);

		String hostname = "fubar";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return metric + "_" + hostname + "_" + dayMs;
	}

	private static void printUsage() {
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory
				.getOperatingSystemMXBean();
		System.out.println(operatingSystemMXBean.getSystemLoadAverage());
		for (Method method : operatingSystemMXBean.getClass()
				.getDeclaredMethods()) {
			method.setAccessible(true);
			if (method.getName().startsWith("get")
					&& Modifier.isPublic(method.getModifiers())) {
				Object value;
				try {
					value = method.invoke(operatingSystemMXBean);
				} catch (Exception e) {
					value = e;
				} // try
				System.out.println(method.getName() + " = " + value);
			} // if
		} // for
	}

}

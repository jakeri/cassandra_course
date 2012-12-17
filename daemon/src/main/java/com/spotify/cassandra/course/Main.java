package com.spotify.cassandra.course;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

public class Main {

    private static final String KEYSPACE_NAME = "JavaKeyspaceName";
    private final Keyspace keyspace;
//    private final Cluster cluster;

    public Main() throws ConnectionException {

        AstyanaxContext.Builder builder = new AstyanaxContext.Builder()
                .forCluster("JavaJava")
                .forKeyspace(KEYSPACE_NAME)
                .withAstyanaxConfiguration(
                        new AstyanaxConfigurationImpl()
                                .setDiscoveryType(NodeDiscoveryType.NONE))
                .withConnectionPoolConfiguration(
                        new ConnectionPoolConfigurationImpl("MyConnectionPool")
                                .setPort(9160).setMaxConnsPerHost(1)
                                .setSeeds("127.0.0.1:9160"))
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor());
        AstyanaxContext<Keyspace> ksContext = builder
				.buildKeyspace(ThriftFamilyFactory.getInstance());

		ksContext.start();
		this.keyspace = ksContext.getEntity();

//        AstyanaxContext<Cluster> clusterContext = builder.buildCluster(ThriftFamilyFactory.getInstance());
//        clusterContext.start();
//        this.cluster = clusterContext.getEntity();
//
//        createColumnFamily(CF_METRIC);
	}

//    private void createColumnFamily(ColumnFamily<String, Long> columnFamily) throws ConnectionException {
//        KeyspaceDefinition ks = cluster.describeKeyspace(KEYSPACE_NAME);
//        if (ks.getColumnFamily(CF_METRIC_NAME) == null) {
//            cluster.addColumnFamily(cluster.makeColumnFamilyDefinition().setKeyspace(keyspace.getKeyspaceName()).setName(CF_METRIC_NAME));
//        }
//    }

    private static final String CF_METRIC_NAME = "Metric";
    public static final ColumnFamily<String, Long> CF_METRIC = new ColumnFamily<String, Long>(
            CF_METRIC_NAME, // Column Family Name
			StringSerializer.get(), // Key Serializer
			LongSerializer.get()); // Column Serializer

	public static final long MS_DAY = 86400000;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

        try {
            Main main = new Main();

            while (true) {
                long time = System.currentTimeMillis();
                main.writeMetric("load", time, Metric.cpuLoad());
                main.writeMetric("freemem", time, Metric.freeMemory());
                //System.out.println(Metric.freeMemory());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Got interrupted");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fail: ", e);
        }

    }

	public void writeMetric(String metric, final long ts, double data) {

		String rowKey = buildRowKey(ts, metric);

		MutationBatch m = keyspace.prepareMutationBatch();

		m.withRow(CF_METRIC, rowKey).putColumn(ts, data, null);

		try {
			OperationResult<Void> result = m.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
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

}

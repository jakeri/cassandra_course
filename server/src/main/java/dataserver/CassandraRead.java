package dataserver;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class CassandraRead {
	
	public static void main(String[] args) throws ConnectionException {
		CassandraRead me = new CassandraRead();
		Keyspace ks = me.init();
		ColumnFamily<String, Long> cf = me.getCf();
		ColumnList<Long> columns = me.readRow(ks, cf, "hj");
		for (Column<Long> c : columns) {
			System.out.println(c.getName());
		}

	}
	
	public Keyspace init() {
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
		Keyspace keyspace = context.getEntity();
		return keyspace;
	}

	public ColumnFamily<String, Long> getCf() {
		ColumnFamily<String, Long> CF_USER_INFO = new ColumnFamily<String, Long>(
				"Metric", // Column Family Name
				StringSerializer.get(), // Key Serializer
				LongSerializer.get()); // Column Serializer
		return CF_USER_INFO;
	}

	public ColumnList<Long> readRow(Keyspace ks, ColumnFamily<String, Long> cf,
			String rowKey) throws ConnectionException {
		OperationResult<ColumnList<Long>> result = ks.prepareQuery(cf)
				.getKey(rowKey).execute();
		ColumnList<Long> columns = result.getResult();
		return columns;
	}
	
}
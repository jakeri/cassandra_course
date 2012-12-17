package dataserver;

import dataserver.webserver.Server;
import org.apache.log4j.BasicConfigurator;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class Main {

	private static final String KEYSPACE_NAME = "JavaKeyspaceName";

	public static void main(String[] args) throws Exception {

		BasicConfigurator.configure();

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

		// Server.run( new DummyStatsService() );
		Server.run(new CassandraStatsService(ksContext.getEntity()));

	}

}

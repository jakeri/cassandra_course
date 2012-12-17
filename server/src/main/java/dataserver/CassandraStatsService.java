package dataserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;

public class CassandraStatsService implements StatsService {

	public static final long MS_DAY = 86400000L;
	private final Keyspace keyspace;

	private static final ColumnFamily<String, Long> CF_METRIC = new ColumnFamily<String, Long>(
			"Metric", // Column Family Name
			StringSerializer.get(), // Key Serializer
			LongSerializer.get()); // Column Serializer

	public CassandraStatsService(Keyspace keyspace) {
		super();
		this.keyspace = keyspace;
	}

	public MetaResult getMetaData() throws IOException {

        return new MetaResult(
                ImmutableSet.of("Jakobs-MacBook-Air.local"),
                ImmutableSet.of("load","freemem")
            );
	}

	public ImmutableList<MetricTuple> getStatsFor(String host, String metric) {

		// this cassandra datamodel is using daily row key rotation, but
		// incoming does not
		// Therefore set a start value and end value for day.

		long startTime = 1355391884000L;
		long endTime = System.currentTimeMillis();

		List<String> rowKeys = calculateRowKeys(startTime, endTime, host,
				metric);

		Builder<MetricTuple> builder = ImmutableList.builder();
		try {
			OperationResult<Rows<String, Long>> results = keyspace
					.prepareQuery(CF_METRIC).getKeySlice(rowKeys).execute();
			for (Row<String, Long> row : results.getResult()) {
				System.out.println(row.getKey());
				for (Column<Long> column : row.getColumns()) {
					//System.out.println(column.getName());
					builder.add(new MetricTuple(column.getName(), column.getDoubleValue()));
				}
			}
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}

		return builder.build();
	}

	private List<String> calculateRowKeys(long startTime, long endTime,
			String host, String metric) {

		List<String> days = new ArrayList<String>();

		long day = startTime - (startTime % MS_DAY);
		while (day < endTime) {
			days.add(metric + "_" + host + "_" + day);
			day = day + MS_DAY;
			

		}
		// metric + "_" + hostname + "_" + dayMs;
		return days;
	}

}

package dataserver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;

public interface StatsService {
    MetaResult                  getMetaData() throws IOException;
    ImmutableList<MetricTuple>  getStatsFor(String host, String metric);

    public class MetaResult{
        public final ImmutableSet<String> hostnames;
        public final ImmutableSet<String> metricName;
        public MetaResult(ImmutableSet<String> hostnames, ImmutableSet<String> metricNames) {
          this.hostnames = hostnames;
          this.metricName = metricNames;
        }
    }

    public class MetricTuple {
        public final Long timestamp;
        public final Double value;

        public MetricTuple(Long timestamp, Double value){
            this.timestamp = timestamp;
            this.value = value;
        }
    }
}


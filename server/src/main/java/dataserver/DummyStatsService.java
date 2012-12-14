package dataserver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;

public class DummyStatsService implements StatsService {

    @Override
    public MetaResult getMetaData() throws IOException {
        return new MetaResult(
            ImmutableSet.of("host1","host2","host3"),
            ImmutableSet.of("metric1","metric2")
        );
    }

    @Override
    public ImmutableList<MetricTuple> getStatsFor(String host, String metric) {
        ImmutableList.Builder tmp = new ImmutableList.Builder<MetricTuple>();
        for (long i = 0 ;i< 10000; i ++ ) {
            if ("metric1".equals(metric)){
                tmp.add( new MetricTuple(i, Math.sin(i) * 100));
            } else {
                tmp.add( new MetricTuple(i, Math.cos(i) * 50));
            }
        }
        return tmp.build();
    }
}

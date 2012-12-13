package com.spotify.cassandra.course;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Metric {
    private static Sigar sigar = new Sigar();

    public static double cpuLoad() {
        try {
            return sigar.getCpuPerc().getCombined();
        } catch (SigarException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}


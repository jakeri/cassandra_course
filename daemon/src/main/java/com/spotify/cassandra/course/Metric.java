package com.spotify.cassandra.course;

import java.lang.management.ManagementFactory;

public class Metric {
    public static double cpuLoad() {
        return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    }

    public static long freeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}


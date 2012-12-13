package com.spotify.cassandra.course;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

	
	public static final long MS_DAY = 86400000;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		printUsage();
		

		System.out.println(buildRowKey("apa"));
		
	}
	
	
	
	public void writeMetric(String metric, long data) {
		
		
		
	}
	
	public static String buildRowKey(String metric) {

		long now = System.currentTimeMillis();
		
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
		  OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		  System.out.println(operatingSystemMXBean.getSystemLoadAverage());
		  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
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

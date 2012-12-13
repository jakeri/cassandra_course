package dataserver;


import dataserver.webserver.Server;
import org.apache.log4j.BasicConfigurator;


public class Main {


	public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();

        // TODO: let CassandraRead implement StatsService and inject into the Server instead of DummyService
        Server.run( new DummyStatsService()  );

	}



}

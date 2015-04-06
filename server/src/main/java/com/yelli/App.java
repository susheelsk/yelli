package com.yelli;

import java.io.IOException;
import java.util.logging.Logger;


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.tyrus.server.Server;

import com.yelli.apis.CreateTrackerApi;
import com.yelli.apis.TestServelet;
import com.yelli.apis.UpdateLocationApi;

/**
 * Hello world!
 * 
 */
public class App {

	private static Logger logger = Logger.getLogger(App.class.getSimpleName());

	public static void main(String[] args) {
		try {
			runHttpServer();
			runServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void runServer() throws InterruptedException {
		Server server = new Server("localhost", 10023, "/yelli", YelliWebsocketApi.class);

		try {
			server.start();
			logger.info("Server Starting");
			Thread.currentThread().join();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			server.stop();
		}
	}

	private static void runHttpServer() throws IOException, InterruptedException {
		logger.info("Http Server starting");
		final HttpServer server = HttpServer.createSimpleServer("/",10024);
		final ServerConfiguration config = server.getServerConfiguration();
		config.addHttpHandler(new UpdateLocationApi(), "/yelli/update");
		config.addHttpHandler(new CreateTrackerApi(), "/yelli/create");
		config.addHttpHandler(new TestServelet(), "/yelli/test");
		config.setJmxEnabled(true);
		server.start();
	}

}

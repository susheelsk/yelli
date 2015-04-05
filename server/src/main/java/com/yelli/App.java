package com.yelli;

import java.util.logging.Logger;

import org.glassfish.tyrus.server.Server;

/**
 * Hello world!
 * 
 */
public class App {
	
	private static Logger logger = Logger.getLogger(App.class.getSimpleName());
	
	public static void main(String[] args) {
		runServer();
	}

	private static void runServer() {
		Server server = new Server("localhost", 10023, "/yelli", YelliWebSocketApi.class);
		
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
	
}

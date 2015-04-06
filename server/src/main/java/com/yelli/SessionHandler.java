package com.yelli;


//public class SessionHandler implements WebSocket.OnTextMessage{
//
//	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());
//	private JettySession session;
//
//	@Override
//	public void onOpen(Connection connection) {
//		String sessionId = UUID.randomUUID().toString();
//		session = new JettySession(connection, sessionId);
//		logger.info("Connected ... " + sessionId);
//	}
//
//	@Override
//	public void onMessage(String message) {
//		RequestPojo requestPojo = new Gson().fromJson(message, RequestPojo.class);
//		OnAction onAction = null;
//		switch (requestPojo.type) {
//		case CREATE:
//			logger.info("Create Tracker with sessionId : " + session.getId());
//			onAction = new CreateTrackerAction();
//			break;
//		case SUBSCRIBE:
//			logger.info("Subscribe Tracker with sessionId : " + session.getId());
//			onAction = new SubscribeTrackerAction();
//			break;
//		case UPDATE:
//			logger.info("Update Tracker with sessionId : " + session.getId());
//			onAction = new UpdateTrackerAction();
//			break;
//		case PING:
//			logger.info("Ping from sessionId : " + session.getId());
//			onAction = new PingTrackerAction();
//			break;
//		default:
//			logger.info("Unsupported action. Weird! Call the guy who wrote this");
//			return;
//		}
//		onAction.onMessage(message, session);
//	}
//	
//	@Override
//	public void onClose(int closeCode, String message) {
//		logger.info("Disconnected from sessionId : "+session.getId());
//	}
//
//}

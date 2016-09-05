package utils;

import com.practicaSV.gameLabz.domain.User;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import com.practicaSV.gameLabz.utils.PathConstants;
import org.junit.Assert;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import utils.websocket.AbstractFrameHandler;
import utils.websocket.FrameHandlerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TestUser {

    private User user;

    private String sessionId;

    private StompSession stompSession;

    private Map<String, ArrayBlockingQueue> messageMap;

    private Map<String, StompSession.Subscription> subscriptionMap;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public static final String URL = "ws://localhost:8080/gameLabz/ws";

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void connectWS(String url) {

        messageMap = new HashMap<>();
        subscriptionMap = new HashMap<>();

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.set(PathConstants.USER_NAME_KEY, user.getUserName());
        headers.set(HttpHeadersConstants.SESSION_ID, sessionId);
        try {

            stompSession = stompClient.connect(url, headers, new StompSessionHandlerAdapter() {
            }).get(10, TimeUnit.SECONDS);

        } catch (Exception e) {
            Assert.fail("Failed to connect WebSocket: " + e.getMessage());
        }

        logger.debug("Connected to "+url);
    }

    public void subscribe(String topicName) {

        if (stompSession == null || !stompSession.isConnected()) {
            connectWS(URL);
        }

        String topicPath = "/topic/"+user.getUserName()+topicName;

        AbstractFrameHandler frameHandler = FrameHandlerFactory.build(topicName);
        if (frameHandler == null) {
            Assert.fail("Failed to build frame handler!");
        }
        ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(10);
        frameHandler.setArrayBlockingQueue(arrayBlockingQueue);

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(PathConstants.USER_NAME_KEY, user.getUserName());
        stompHeaders.add(HttpHeadersConstants.SESSION_ID, sessionId);
        stompHeaders.set(StompHeaders.DESTINATION, topicPath);

        StompSession.Subscription subscription = stompSession.subscribe(stompHeaders, frameHandler);

        subscriptionMap.put(topicPath, subscription);
        messageMap.put(topicPath, arrayBlockingQueue);
        logger.debug("Subscribed to "+topicPath);
    }

    public void unSubscribe(String topicName) {

        String topicPath = "/topic/"+user.getUserName()+topicName;

        subscriptionMap.get(topicPath).unsubscribe();
        subscriptionMap.remove(topicPath);
        messageMap.remove(topicPath);
    }

    public void disconnect() {
        stompSession.disconnect();
    }

    public <T> T getMessageFromTopic(String topicName, Class<T> clazz) {

        String topicPath = "/topic/"+user.getUserName()+topicName;

        ArrayBlockingQueue queue = messageMap.get(topicPath);

        if (queue == null) {
            Assert.fail("Topic: "+topicPath+" not found!");
        }
        try {
            return (T) queue.poll(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Assert.fail("Timeout while getting message from "+topicPath);
        }
        return null;
    }
}

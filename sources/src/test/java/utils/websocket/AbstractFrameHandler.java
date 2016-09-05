package utils.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class AbstractFrameHandler<T> implements StompFrameHandler {

    private ArrayBlockingQueue<T> arrayBlockingQueue;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {

        logger.debug(o.toString());
        arrayBlockingQueue.add((T) o);
    }

    public void setArrayBlockingQueue(ArrayBlockingQueue<T> arrayBlockingQueue) {
        this.arrayBlockingQueue = arrayBlockingQueue;
    }
}

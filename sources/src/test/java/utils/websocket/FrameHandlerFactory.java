package utils.websocket;

import com.practicaSV.gameLabz.domain.FriendRequest;
import com.practicaSV.gameLabz.domain.GameGiftNotification;
import com.practicaSV.gameLabz.utils.websocket.TopicConstants;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

public class FrameHandlerFactory {

    public static AbstractFrameHandler build(String topicName) {

        switch (topicName) {

            case TopicConstants.FRIEND_REQUEST:
                return new AbstractFrameHandler<FriendRequest>() {
                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        return FriendRequest.class;
                    }
                };
            case TopicConstants.GAME_GIFT:
                return new AbstractFrameHandler<GameGiftNotification>() {
                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        return GameGiftNotification.class;
                    }
                };
            default:
                return null;
        }
    }
}

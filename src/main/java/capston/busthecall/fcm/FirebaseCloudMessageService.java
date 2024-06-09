package capston.busthecall.fcm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/busthecall-62ef5/messages:send";
    private final ObjectMapper objectMapper;
    private final FirebaseMessaging firebaseMessaging;

    public void sendMessageTo(String targetToken, String title, String body) {
        log.info("correct send message to");

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(notification)
                .setApnsConfig(createApns())
                .build();
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.info("알림 보내기를 실패하였습니다. targetToken : {}", targetToken);
        }
    }

    private ApnsConfig createApns() {
        return ApnsConfig.builder()
                .putHeader("apns-priority", "10")
                .setAps(Aps.builder()
                        .setBadge(1)
                        .setSound("Unibus_알림.wav")
                        .build())
                .build();
    }
}

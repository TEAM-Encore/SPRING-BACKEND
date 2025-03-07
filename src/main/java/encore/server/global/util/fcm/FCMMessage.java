package encore.server.global.util.fcm;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record FCMMessage (
        boolean validateOnly,
        Message message
){
    @Builder
    public record Message(
            Notification notification,
            String token
    ){
        @Builder
        public record Notification(
                String title,
                String body,
                String image
        ){
            public static Notification createNotification(String title, String body, String image){
                return Notification.builder()
                        .title(title)
                        .body(body)
                        .image(image)
                        .build();
            }
        }

        public static Message createMessage(String title, String body, String image, String token){
            return Message.builder()
                    .notification(Notification.createNotification(title, body, image))
                    .token(token)
                    .build();
        }
    }

    public static FCMMessage of(String title, String body, String image, String token){
        return FCMMessage.builder()
                .validateOnly(false)
                .message(Message.createMessage(title, body, image, token))
                .build();
    }
}

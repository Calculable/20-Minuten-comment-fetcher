package ch.ost.newspapercommenttodatabasefetcher.service;

import ch.ost.newspapercommenttodatabasefetcher.exceptions.CannotSendNotificationToMicrosoftTeams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
/**
 * Sends a notification to a Microsoft Teams Webhook
 */
public class MicrosoftTeamsNotificationSender {

    /**
     * Sends a notification to a Microsoft Teams Channel
     *
     * @param webhookUrl a webhook of the channel
     * @param title      title of the notification
     * @param text       body / main content of the notification
     * @param themeColor color code for the notification. For example, red would be: "#FF0000"
     * @throws CannotSendNotificationToMicrosoftTeams if the notification cannot be sent
     */
    public void sendMicrosoftTeamsNotification(String webhookUrl, String title, String text, String themeColor) throws CannotSendNotificationToMicrosoftTeams {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("title", title);
        postParameters.put("text", text);
        postParameters.put("themeColor", themeColor);

        ResponseEntity<Void> response = restTemplate.postForEntity(webhookUrl, postParameters, Void.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new CannotSendNotificationToMicrosoftTeams();
        }
    }
}

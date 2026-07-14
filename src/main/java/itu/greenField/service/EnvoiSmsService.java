package itu.greenField.service;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Service
public class EnvoiSmsService {

    private static final String URL = "https://api.textbee.dev/api/v1/gateway/devices/6a562db0806d9579a8a6c7d2/send-sms";

    private static final String API_KEY = "d6df50d4-0215-4b60-886f-db8ff54e940b";

    private final RestTemplate restTemplate;

    public EnvoiSmsService() {
        this.restTemplate = new RestTemplate();
    }

    @Async
    public void envoyerSms(String numero, String txtMessage) {

        try {

            System.out.println(
                    "Thread SMS : "
                            + Thread.currentThread().getName());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", API_KEY);

            Map<String, Object> body = Map.of(
                    "recipients",
                    List.of(numero),

                    "message",
                    txtMessage);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    URL,
                    request,
                    String.class);

            System.out.println(
                    "SMS envoyé : "
                            + response.getBody());

        } catch (Exception e) {

            System.err.println(
                    "Erreur SMS : "
                            + e.getMessage());
        }
    }
}
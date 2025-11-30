package com.example.medmeproject.Service;

// Conceptual Java Code (NOT a runnable file block)

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SmsService {

    // Map to hold active Emitters, keyed by a unique identifier (e.g., identityCard)
    private final ConcurrentMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final long TIMEOUT = 1800000L; // 30 minutes

    public SseEmitter addEmitter(String identityCard) {
        // Use the identity card as the key for the user's stream
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.put(identityCard, emitter);

        emitter.onCompletion(() -> emitters.remove(identityCard));
        emitter.onTimeout(() -> {
            emitters.remove(identityCard);
            emitter.complete();
        });

        // Initial dummy data to complete the handshake
        try {
            emitter.send(SseEmitter.event().name("CONNECT").data("Connection established."));
        } catch (IOException e) {
            emitters.remove(identityCard);
            emitter.complete();
        }

        return emitter;
    }

    // Method to simulate sending the SMS by pushing to the client
    public void sendSms(String identityCard, String code, String phoneNumber) {
        SseEmitter emitter = emitters.get(identityCard);
        if (emitter != null) {
            try {
                String payload = String.format("{\"code\": \"%s\", \"to\": \"%s\"}", code, phoneNumber);

                // The client side listener looks for the event name 'authCode'
                emitter.send(SseEmitter.event()
                        .name("authCode")
                        .data(payload)
                        .id(String.valueOf(System.currentTimeMillis())));

            } catch (IOException e) {
                emitters.remove(identityCard);
                emitter.completeWithError(e);
            }
        }
    }    // Method to simulate sending the SMS by pushing to the client
    public void sendUpdateSms(String identityCard, String message, String phoneNumber) {
        SseEmitter emitter = emitters.get(identityCard);
        if (emitter != null) {
            try {
                String payload = String.format("{\"message\": \"%s\", \"to\": \"%s\"}", message, phoneNumber);

                // The client side listener looks for the event name 'authCode'
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(payload)
                        .id(String.valueOf(System.currentTimeMillis())));

            } catch (IOException e) {
                emitters.remove(identityCard);
                emitter.completeWithError(e);
            }
        }
    }
}


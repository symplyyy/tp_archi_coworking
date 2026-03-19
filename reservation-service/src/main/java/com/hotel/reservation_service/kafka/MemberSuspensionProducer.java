package com.hotel.reservation_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MemberSuspensionProducer {

    private static final String MEMBER_SUSPENSION_TOPIC = "member-suspension";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public MemberSuspensionProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishSuspensionUpdate(Long memberId, boolean suspended) {
        try {
            MemberSuspensionEvent event = new MemberSuspensionEvent(memberId, suspended);
            kafkaTemplate.send(MEMBER_SUSPENSION_TOPIC, objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

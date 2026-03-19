package com.hotel.member_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.member_service.repository.MemberRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MemberSuspensionConsumer {

    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    public MemberSuspensionConsumer(MemberRepository memberRepository, ObjectMapper objectMapper) {
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "member-suspension", groupId = "member-service")
    public void handleSuspensionUpdate(String message) {
        try {
            MemberSuspensionEvent event = objectMapper.readValue(message, MemberSuspensionEvent.class);
            memberRepository.findById(event.getMemberId()).ifPresent(member -> {
                member.setSuspended(event.isSuspended());
                memberRepository.save(member);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

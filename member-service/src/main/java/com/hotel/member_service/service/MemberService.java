package com.hotel.member_service.service;

import com.hotel.member_service.exception.ResourceNotFoundException;
import com.hotel.member_service.kafka.MemberEventProducer;
import com.hotel.member_service.model.Member;
import com.hotel.member_service.model.SubscriptionType;
import com.hotel.member_service.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberEventProducer memberEventProducer;

    public MemberService(MemberRepository memberRepository, MemberEventProducer memberEventProducer) {
        this.memberRepository = memberRepository;
        this.memberEventProducer = memberEventProducer;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membre introuvable : " + id));
    }

    public Member createMember(Member member) {
        member.setMaxConcurrentBookings(resolveMaxBookings(member.getSubscriptionType()));
        member.setSuspended(false);
        return memberRepository.save(member);
    }

    public Member updateMember(Long id, Member updatedMember) {
        Member member = getMemberById(id);
        member.setFullName(updatedMember.getFullName());
        member.setEmail(updatedMember.getEmail());
        member.setSubscriptionType(updatedMember.getSubscriptionType());
        member.setMaxConcurrentBookings(resolveMaxBookings(updatedMember.getSubscriptionType()));
        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        Member member = getMemberById(id);
        memberRepository.delete(member);
        memberEventProducer.publishMemberDeleted(id);
    }

    public Member updateSuspension(Long id, boolean suspended) {
        Member member = getMemberById(id);
        member.setSuspended(suspended);
        return memberRepository.save(member);
    }

    private int resolveMaxBookings(SubscriptionType type) {
        return switch (type) {
            case BASIC -> 2;
            case PRO -> 5;
            case ENTERPRISE -> 10;
        };
    }
}

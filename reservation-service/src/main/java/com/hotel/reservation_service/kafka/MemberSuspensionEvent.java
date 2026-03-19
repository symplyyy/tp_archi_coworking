package com.hotel.reservation_service.kafka;

public class MemberSuspensionEvent {

    private Long memberId;
    private boolean suspended;

    public MemberSuspensionEvent() {}

    public MemberSuspensionEvent(Long memberId, boolean suspended) {
        this.memberId = memberId;
        this.suspended = suspended;
    }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }
}

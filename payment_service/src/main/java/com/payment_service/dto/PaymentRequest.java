package com.payment_service.dto;

public class PaymentRequest {
    private Long bookingId;
    private MemberDetails memberDetails;
    private PlanDetails planDetails;
    private PaymentSummary paymentSummary;
    private String startDate;
    private boolean isAutoRenewalActive;

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public MemberDetails getMemberDetails() { return memberDetails; }
    public void setMemberDetails(MemberDetails memberDetails) { this.memberDetails = memberDetails; }
    public PlanDetails getPlanDetails() { return planDetails; }
    public void setPlanDetails(PlanDetails planDetails) { this.planDetails = planDetails; }
    public PaymentSummary getPaymentSummary() { return paymentSummary; }
    public void setPaymentSummary(PaymentSummary paymentSummary) { this.paymentSummary = paymentSummary; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public boolean isAutoRenewalActive() { return isAutoRenewalActive; }
    public void setAutoRenewalActive(boolean autoRenewalActive) { isAutoRenewalActive = autoRenewalActive; }
}
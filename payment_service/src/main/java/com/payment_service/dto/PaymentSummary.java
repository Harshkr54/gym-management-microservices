package com.payment_service.dto;

public class PaymentSummary {
    private Double baseAmount;
    private Double taxAmount;
    private String discountCode;
    private Double discountAmount;
    private Double finalPayableAmount;
    private String currency;
    private Long quantity;

    public Double getBaseAmount() { return baseAmount; }
    public void setBaseAmount(Double baseAmount) { this.baseAmount = baseAmount; }
    public Double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }
    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    public Double getFinalPayableAmount() { return finalPayableAmount; }
    public void setFinalPayableAmount(Double finalPayableAmount) { this.finalPayableAmount = finalPayableAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Long getQuantity() { return quantity; }
    public void setQuantity(Long quantity) { this.quantity = quantity; }
}
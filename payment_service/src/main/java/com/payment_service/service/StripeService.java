package com.payment_service.service;

import com.payment_service.dto.PaymentRequest;
import com.payment_service.dto.PaymentResponse;
import com.payment_service.entity.PaymentRecord;
import com.payment_service.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    private final PaymentRepository paymentRepository;

    // Constructor Injection is the best practice for Spring Boot
    public StripeService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // This runs once when the application starts up
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public PaymentResponse checkoutProducts(PaymentRequest request) {

        // 1. Extract exactly what we need from the nested JSON objects
        Double finalAmount = request.getPaymentSummary().getFinalPayableAmount();
        String planName = request.getPlanDetails().getPlanName();
        String currency = request.getPaymentSummary().getCurrency();
        Long quantity = request.getPaymentSummary().getQuantity();
        String customerEmail = request.getMemberDetails().getEmail();

        // 2. Convert the Rupee amount (Double) to Paise (Long) safely for Stripe
        long amountInPaise = Math.round(finalAmount * 100);

        // 3. Build the Stripe Line Items
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(planName)
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(currency != null ? currency : "INR")
                        .setUnitAmount(amountInPaise)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(quantity)
                .setPriceData(priceData)
                .build();

        // 4. Configure the Checkout Session Behavior
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // These URLs tell Stripe where to send the user after they pay
                .setSuccessUrl("http://localhost:3000/payment-success?session_id={CHECKOUT_SESSION_ID}&bookingId=" + request.getBookingId())
                .setCancelUrl("http://localhost:3000/payment-cancel")
                // This injects the user's email straight into the Stripe UI!
                .setCustomerEmail(customerEmail)
                .addLineItem(lineItem)
                .build();

        try {
            // 5. Call the Stripe API to generate the URL
            Session session = Session.create(params);

            // 6. Save a "PENDING" transaction record to your MySQL Database
            PaymentRecord record = new PaymentRecord();
            record.setBookingId(request.getBookingId());
            record.setStripeSessionId(session.getId());
            record.setStatus("PENDING");

            // Map Member Info
            record.setMemberId(request.getMemberDetails().getMemberId());
            record.setMemberEmail(customerEmail);

            // Map Plan Info
            record.setPlanName(planName);
            // Converts the "2026-04-01" string from JSON into a Java LocalDate object
            record.setStartDate(LocalDate.parse(request.getStartDate()));
            record.setAutoRenewalActive(request.isAutoRenewalActive());

            // Map Financial Breakdown
            record.setBaseAmount(request.getPaymentSummary().getBaseAmount());
            record.setTaxAmount(request.getPaymentSummary().getTaxAmount());
            record.setDiscountCode(request.getPaymentSummary().getDiscountCode());
            record.setDiscountAmount(request.getPaymentSummary().getDiscountAmount());
            record.setFinalPayableAmount(finalAmount);
            record.setCurrency(currency);

            // Save to DB!
            paymentRepository.save(record);

            // 7. Send the URL back to your Controller (and eventually the React frontend)
            PaymentResponse response = new PaymentResponse();
            response.setStatus("SUCCESS");
            response.setMessage("Payment session created successfully");
            response.setSessionId(session.getId());
            response.setSessionUrl(session.getUrl());

            return response;

        } catch (StripeException e) {
            // Throwing a RuntimeException allows the Controller's @ExceptionHandler or Try/Catch to handle it
            throw new RuntimeException("Failed to initialize Stripe checkout: " + e.getMessage());
        }
    }

    // This method is called by the Controller when the user returns from Stripe
    public void updatePaymentStatus(String sessionId, String status) {
        paymentRepository.findByStripeSessionId(sessionId).ifPresent(record -> {
            record.setStatus(status);
            paymentRepository.save(record);
        });
    }
}
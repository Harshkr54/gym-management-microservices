package com.payment_service.controller;

import com.payment_service.dto.PaymentRequest;
import com.payment_service.dto.PaymentResponse;
import com.payment_service.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final StripeService stripeService;
    // @Autowired
    // private BookingClient bookingClient; // Uncomment when your Feign client is ready

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<PaymentResponse> checkoutProducts(@RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = stripeService.checkoutProducts(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            PaymentResponse error = new PaymentResponse();
            error.setStatus("FAILED");
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> handleSuccess(@RequestParam("session_id") String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);

            if ("paid".equalsIgnoreCase(session.getPaymentStatus())) {
                // 1. Update DB to SUCCESS
                stripeService.updatePaymentStatus(sessionId, "SUCCESS");

                // 2. Call other microservice via Feign to confirm the gym booking
                // bookingClient.confirmBooking(bookingId);

                return ResponseEntity.ok("Payment successful and verified!");
            } else {
                stripeService.updatePaymentStatus(sessionId, "FAILED");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment not completed");
            }

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Verification error");
        }
    }
}
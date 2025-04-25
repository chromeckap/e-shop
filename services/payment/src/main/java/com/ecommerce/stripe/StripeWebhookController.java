package com.ecommerce.stripe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stripe-webhook")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {
    private final StripeWebhookService stripeWebhookService;

    @PostMapping
    public void handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signatureHeader
    ) {
        log.info("Received Stripe webhook for event: {}", signatureHeader);
        stripeWebhookService.handleWebhook(payload, signatureHeader);
    }
}

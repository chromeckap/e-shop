package com.ecommerce.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplates {
    ORDER_CONFIRMATION("order-confirmation.html", "Potvrzení o přijetí objednávky"),
    PAYMENT_CREATED("payment-created.html", "Informace o platbě"),
    PAYMENT_SUCCESSFUL("payment-successful.html", "Potvrzení o zaplacení objednávky");

    private final String template;
    private final String subject;
}

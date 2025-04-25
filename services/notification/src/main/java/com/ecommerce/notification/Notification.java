package com.ecommerce.notification;


import com.ecommerce.kafka.order.OrderConfirmation;
import com.ecommerce.kafka.payment.PaymentConfirmation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    @Field("type")
    private NotificationType type;

    @Field("date")
    private LocalDateTime date;

    @Field("orderConfirmation")
    private OrderConfirmation orderConfirmation;

    @Field("paymentConfirmation")
    private PaymentConfirmation paymentConfirmation;

}

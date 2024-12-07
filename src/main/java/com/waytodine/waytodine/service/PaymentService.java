package com.waytodine.waytodine.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.waytodine.waytodine.dto.PaymentResponse;
import com.waytodine.waytodine.model.Order;
import org.springframework.beans.factory.annotation.Value;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {


    @Value("${stripe.api.key}")
    private String stripeSecretKey;


    public PaymentResponse createPaymentLink(Order order)
    {
        Stripe.apiKey=stripeSecretKey;
        SessionCreateParams params = SessionCreateParams.builder().addPaymentMethodType(
                SessionCreateParams.
                        PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payment/success/"+order.getOrderId())
                .setCancelUrl("http://localhost:3000/payment/fail")
                .putMetadata("orderId", order.getOrderId().toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("inr")
                                                .setUnitAmount((long)order.getAmount() * 100) // Amount in the smallest currency unit (e.g., cents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Order Payment")
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                )
                .build();

        try {
            Session session = Session.create(params);
            PaymentResponse res = new PaymentResponse();
            res.setPayment_url(session.getUrl());
            return res;
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }
}

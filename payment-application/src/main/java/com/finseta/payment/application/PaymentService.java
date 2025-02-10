package com.finseta.payment.application;

import com.finseta.payment.domain.Payment;
import com.finseta.payment.repository.PaymentRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final Cache<String, Payment> idempotencyCache = Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS) // Expire keys after 24 hours
            .build();
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Mono<Payment> createPayment(Payment payment, String idempotencyKey) {
        if (idempotencyKey != null) {
            Payment cachedPayment = idempotencyCache.getIfPresent(idempotencyKey);
            if (cachedPayment != null) {
                return Mono.just(cachedPayment); // Return cached response
            }
        }

        return paymentRepository.save(payment)
                .doOnNext(savedPayment -> {
                    if (idempotencyKey != null) {
                        idempotencyCache.put(idempotencyKey, savedPayment);
                    }
                });
    }

    public Flux<Payment> getPayments(Double minAmount, List<String> currencies) {
        Flux<Payment> payments = paymentRepository.findAll();
        return payments
                .filter(payment -> (minAmount == null || payment.getAmount() >= minAmount))
                .filter(payment -> (currencies == null || currencies.isEmpty() || currencies.contains(payment.getCurrency())));
    }

}

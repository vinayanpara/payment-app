package com.finseta.payment.repository;

import com.finseta.payment.domain.Payment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentRepository {
    Mono<Payment> save(Payment payment);

    Flux<Payment> findAll();

    void deleteAll();
}

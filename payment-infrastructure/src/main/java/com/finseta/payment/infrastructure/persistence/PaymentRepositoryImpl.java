package com.finseta.payment.infrastructure.persistence;

import com.finseta.payment.domain.Payment;
import com.finseta.payment.repository.PaymentRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final Map<String, Payment> payments = new HashMap<>();
    private static int id = 0;
    @Override
    public Mono<Payment> save(Payment payment) {
        payments.put(String.valueOf(id++), payment);
        return Mono.just(payment);
    }

    @Override
    public Flux<Payment> findAll() {
        return Flux.fromIterable(payments.values());
    }

    @Override
    public void deleteAll() {
        payments.clear();
    }
}

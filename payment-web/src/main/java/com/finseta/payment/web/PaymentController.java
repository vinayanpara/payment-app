package com.finseta.payment.web;

import com.finseta.payment.application.PaymentService;
import com.finseta.payment.domain.Payment;
import com.finseta.payment.validation.ValidCurrency;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Mono<ResponseEntity<Payment>> createPayment(@Valid @RequestBody(required = true) Payment payment,
                                                       @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return paymentService.createPayment(payment, idempotencyKey)
                .map(savedPayment -> ResponseEntity.status(HttpStatus.CREATED).body(savedPayment));
    }

    @GetMapping
    public ResponseEntity<Flux<Payment>> getPayments(
            @Valid @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) List<@ValidCurrency String> currencies) {
        Flux<Payment> payments =  paymentService.getPayments(minAmount, currencies);
        return ResponseEntity.status(HttpStatus.OK).body(payments);
    }

}

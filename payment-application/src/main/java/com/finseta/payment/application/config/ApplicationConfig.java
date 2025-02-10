package com.finseta.payment.application.config;

import com.finseta.payment.application.PaymentService;
import com.finseta.payment.repository.PaymentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public PaymentService paymentService(PaymentRepository paymentRepository) {
        return new PaymentService(paymentRepository);
    }
}
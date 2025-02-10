package com.finseta.payment.infrastructure;

import com.finseta.payment.infrastructure.persistence.PaymentRepositoryImpl;
import com.finseta.payment.repository.PaymentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {
    @Bean
    public PaymentRepository paymentRepository() {
        return new PaymentRepositoryImpl();
    }
}

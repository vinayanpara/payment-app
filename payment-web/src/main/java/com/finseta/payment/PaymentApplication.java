package com.finseta.payment;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.finseta.payment.infrastructure",
        "com.finseta.payment.application",
        "com.finseta.payment"
})
public class PaymentApplication {
    public static void main(String[] args) {
        System.out.println("Running application");
        SpringApplication.run(PaymentApplication.class, args);
    }
}

package com.finseta.payment.web;

import com.finseta.payment.domain.Account;
import com.finseta.payment.domain.AccountType;
import com.finseta.payment.domain.Payment;
import com.finseta.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
public class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    public void setUp() {
        paymentRepository.deleteAll();
        getTestPayments().forEach(payment -> paymentRepository.save(payment));
    }

    private List<Payment> getTestPayments() {
        return Arrays.asList(
                new Payment( 100.50, "USD", new Account("12345678", "123456", AccountType.SORT_CODE_ACCOUNT_NUMBER)),
                new Payment( 200.75, "GBP", new Account("87654321", "654321", AccountType.SORT_CODE_ACCOUNT_NUMBER)),
                new Payment( 50.00, "EUR", new Account("11112222", "112233", AccountType.SORT_CODE_ACCOUNT_NUMBER))
        );
    }

    @Test
    public void testGetAllPayments() {
        webTestClient.get()
                .uri("/payments")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Payment.class)
                .hasSize(3); // Verify that 3 payments are returned
    }

    @Test
    public void testGetPaymentsByCurrency() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/payments")
                        .queryParam("currencies", "USD", "GBP")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Payment.class)
                .hasSize(2) // Verify that 2 payments are returned (USD and GBP)
                .consumeWith(response -> {
                    List<Payment> payments = response.getResponseBody();
                    assert payments.stream().allMatch(p -> List.of("USD", "GBP").contains(p.getCurrency()));
                });
    }

    @Test
    public void testGetPaymentsByMinAmount() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/payments")
                        .queryParam("minAmount", 100.00)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Payment.class)
                .hasSize(2) // Verify that 2 payments are returned (amount >= 100.00)
                .consumeWith(response -> {
                    List<Payment> payments = response.getResponseBody();
                    assert payments.stream().allMatch(p -> p.getAmount() >= 100.00);
                });
    }

    @Test
    public void testGetPaymentsByCurrencyAndMinAmount() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/payments")
                        .queryParam("currencies", "USD", "GBP")
                        .queryParam("minAmount", 100.00)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Payment.class)
                .hasSize(2) // Verify that 2 payments are returned (USD and GBP with amount >= 100.00)
                .consumeWith(response -> {
                    List<Payment> payments = response.getResponseBody();
                    assert payments.stream().allMatch(p -> List.of("USD", "GBP").contains(p.getCurrency()) && p.getAmount() >= 100.00);
                });
    }

    @Test
    public void testCreatePayment() {
        Payment payment = new Payment( 100.50, "USD", new Account("12345678", "123456", AccountType.SORT_CODE_ACCOUNT_NUMBER));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Payment.class)
                .consumeWith(response -> {
                    Payment savedPayment = response.getResponseBody();
                    assert Objects.equals(savedPayment.getAmount(), 100.50);
                    assert Objects.equals(savedPayment.getCurrency(), "USD");
                });
    }

    @Test
    public void testCreatePaymentWithInvalidCurrency() {
        Payment payment = new Payment(100.50, "US", new Account("12345678", "123456", AccountType.SORT_CODE_ACCOUNT_NUMBER));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[0].message").value(o -> {
                    assert Objects.equals(List.of("Currency must be exactly 3 characters long", "Currency must be a valid 3-letter ISO 4217 code").contains((String) o), Boolean.TRUE);
                });
    }

    @Test
    public void testCreatePaymentWithInvalidAmount() {
        Payment payment = new Payment(0.00, "USD", new Account("12345678", "123456", AccountType.SORT_CODE_ACCOUNT_NUMBER));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Amount must be greater than 0.00");
    }

    @Test
    public void testCreatePaymentWithoutCounterParty() {
        Payment payment = new Payment(10.00, "USD", null);

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Counterparty is required");
    }

    @Test
    public void testCreatePaymentWithInvalidAccountNumber() {
        Payment payment = new Payment(10.00, "USD", new Account("1234", "123456", AccountType.SORT_CODE_ACCOUNT_NUMBER));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Account number must be 8 numeric characters");
    }

    @Test
    public void testCreatePaymentWithMissingAccountNumber() {
        Payment payment = new Payment(10.00, "USD", new Account(null, "123456", AccountType.SORT_CODE_ACCOUNT_NUMBER));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Account number is required");
    }

    @Test
    public void testCreatePaymentWithNonNumericAccountNumber() {
        Payment payment = new Payment(10.00, "USD", new Account("abcdefgh", "123456", AccountType.SORT_CODE_ACCOUNT_NUMBER));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Account number must contain only numeric characters");
    }

    @Test
    public void testCreatePaymentWithInvalidSortCode() {
        Payment payment = new Payment(10.00, "USD", new Account("12345678", "123", AccountType.SORT_CODE_ACCOUNT_NUMBER));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Sort code must be 6 numeric characters");
    }

    @Test
    public void testCreatePaymentWithMissingSortCode() {
        Payment payment = new Payment(10.00, "USD", new Account("12345678", null, AccountType.SORT_CODE_ACCOUNT_NUMBER));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Sort code is required");
    }

    @Test
    public void testCreatePaymentWithNonNumericSortCode() {
        Payment payment = new Payment(10.00, "USD", new Account("12345678", "abcdef", AccountType.SORT_CODE_ACCOUNT_NUMBER));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Sort code must contain only numeric characters");
    }

    @Test
    public void testCreatePaymentWithMissingAccountType() {
        Payment payment = new Payment(10.00, "USD", new Account("12345678", "123456", null));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Account type must be SORT_CODE_ACCOUNT_NUMBER");
    }

    @Test
    public void testCreatePaymentWithIdempotencyKey() {
        Payment payment = new Payment(100.50, "USD", new Account("12345678", "123456", AccountType.SORT_CODE_ACCOUNT_NUMBER));
        String idempotencyKey = "abc123";

        // First request
        webTestClient.post()
                .uri("/payments")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Payment.class)
                .consumeWith(response -> {
                    Payment savedPayment = response.getResponseBody();
                    assert savedPayment != null;
                });

        // Second request with the same idempotency key
        webTestClient.post()
                .uri("/payments")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(payment), Payment.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Payment.class)
                .consumeWith(response -> {
                    Payment cachedPayment = response.getResponseBody();
                    assert cachedPayment != null;
                    assertEquals(payment.getAmount(), cachedPayment.getAmount());
                    assertEquals(payment.getCurrency(), cachedPayment.getCurrency());
                });
    }
}

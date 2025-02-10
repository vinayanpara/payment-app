package com.finseta.payment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finseta.payment.validation.ValidCurrency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Objects;

public class Payment {
     @JsonProperty("amount")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Amount must be greater than 0.00")
    private Double amount;
    @JsonProperty("currency")
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters long")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO 4217 code")
    @ValidCurrency
    private String currency;
    @JsonProperty("counterparty")
    @NotNull(message = "Counterparty is required")
    @Valid
    private  Account counterparty;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment that)) return false;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency) && Objects.equals(counterparty, that.counterparty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency, counterparty);
    }


    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCounterparty(Account counterparty) {
        this.counterparty = counterparty;
    }

    public Payment(double amount, String currency, Account counterParty) {
        this.amount = amount;
        this.currency = currency;
        this.counterparty = counterParty;
    }


    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Account getCounterparty() {
        return counterparty;
    }


}

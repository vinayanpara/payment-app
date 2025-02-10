package com.finseta.payment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finseta.payment.validation.ValidAccountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class Account {

    @JsonProperty("accountNumber")
    @NotBlank(message = "Account number is required")
    @Size(min = 8, max = 8, message = "Account number must be 8 numeric characters")
    @Pattern(regexp = "\\d+", message = "Account number must contain only numeric characters")
    private String accountNumber;
    @JsonProperty("sortCode")
    @NotBlank(message = "Sort code is required")
    @Size(min = 6, max = 6, message = "Sort code must be 6 numeric characters")
    @Pattern(regexp = "\\d+", message = "Sort code must contain only numeric characters")
    private String sortCode;
    @JsonProperty("type")
    @NotNull(message = "Account type is required")
    @ValidAccountType
//    @Valid
    private  AccountType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountNumber, account.accountNumber) && Objects.equals(sortCode, account.sortCode) && type == account.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, sortCode, type);
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Account(String accountNumber, String sortCode, AccountType type) {
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.type = type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public AccountType getType() {
        return type;
    }
}

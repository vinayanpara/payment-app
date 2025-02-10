package com.finseta.payment.validation;

import com.finseta.payment.domain.AccountType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountTypeValidator implements ConstraintValidator<ValidAccountType, AccountType> {

    @Override
    public boolean isValid(AccountType value, ConstraintValidatorContext context) {
//        System.out.println("value : " + value.name());
        if (value == null) {
            return false;
        }
//        System.out.println("value 2  : " + String.valueOf(value == AccountType.SORT_CODE_ACCOUNT_NUMBER));
        return value == AccountType.SORT_CODE_ACCOUNT_NUMBER;
    }
}
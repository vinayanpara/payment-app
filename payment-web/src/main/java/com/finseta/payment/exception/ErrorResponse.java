package com.finseta.payment.exception;

import java.util.List;

public class ErrorResponse {

    private List<ErrorDetail> errors;

    public ErrorResponse(List<ErrorDetail> errors) {
        this.errors = errors;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDetail> errors) {
        this.errors = errors;
    }

    public static class ErrorDetail {
        private String message;

        public ErrorDetail(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

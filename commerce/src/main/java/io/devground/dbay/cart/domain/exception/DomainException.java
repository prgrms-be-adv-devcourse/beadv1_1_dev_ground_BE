package io.devground.dbay.cart.domain.exception;

public class DomainException extends RuntimeException {
    private final DomainError errorCode;

    public DomainException(DomainError errorCode) {
        super(errorCode.getHttpStatus() + " : " + errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public DomainException(DomainError errorCode, Throwable cause) {
        super(errorCode.getHttpStatus() + " : " + errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public DomainError getErrorCode() {
        return this.errorCode;
    }
}


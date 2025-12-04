package io.devground.dbay.cart.application.exception;

public class ServiceException extends RuntimeException {
    private final ServiceError errorCode;

    public ServiceException(ServiceError errorCode) {
        super(errorCode.getHttpStatus() + " : " + errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServiceException(ServiceError errorCode, Throwable cause) {
        super(errorCode.getHttpStatus() + " : " + errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ServiceError getErrorCode() {
        return this.errorCode;
    }
}

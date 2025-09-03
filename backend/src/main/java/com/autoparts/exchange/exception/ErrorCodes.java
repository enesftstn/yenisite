package com.autoparts.exchange.exception;

public final class ErrorCodes {
    // Authentication & Authorization
    public static final String INVALID_CREDENTIALS = "AUTH_001";
    public static final String USER_NOT_FOUND = "AUTH_002";
    public static final String EMAIL_ALREADY_EXISTS = "AUTH_003";
    public static final String INVALID_TOKEN = "AUTH_004";
    public static final String ACCESS_DENIED = "AUTH_005";
    
    // Auto Parts
    public static final String AUTOPART_NOT_FOUND = "PART_001";
    public static final String AUTOPART_NOT_AVAILABLE = "PART_002";
    public static final String INSUFFICIENT_QUANTITY = "PART_003";
    public static final String UNAUTHORIZED_PART_ACCESS = "PART_004";
    
    // Orders
    public static final String ORDER_NOT_FOUND = "ORDER_001";
    public static final String INVALID_ORDER_STATUS = "ORDER_002";
    public static final String PAYMENT_FAILED = "ORDER_003";
    public static final String UNAUTHORIZED_ORDER_ACCESS = "ORDER_004";
    
    // Validation
    public static final String VALIDATION_ERROR = "VAL_001";
    public static final String INVALID_INPUT = "VAL_002";
    
    private ErrorCodes() {}
}

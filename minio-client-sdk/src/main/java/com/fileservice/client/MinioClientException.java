package com.fileservice.client;

public class MinioClientException extends RuntimeException {
    
    public MinioClientException(String message) {
        super(message);
    }
    
    public MinioClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

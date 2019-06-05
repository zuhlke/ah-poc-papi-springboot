package com.aimlesshammer.pocpapispringboot.sapis.reactive;

/**
 * Exception thrown by failed API calls
 */
class SapiApiException extends Exception {

    public SapiApiException(String message) {
        super(message);
    }

    public SapiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

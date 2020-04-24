package com.sphereon.libs.did.auth.client.exceptions;

public class MalformedJwtException extends RuntimeException {
    public MalformedJwtException(String message) {
        super(message);
    }
}

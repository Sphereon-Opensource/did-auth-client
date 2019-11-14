package com.sphereon.libs.did.auth.client.exceptions;

public class MalformedLoginJwtException extends RuntimeException {
    public MalformedLoginJwtException(String message){
        super(message);
    }
}

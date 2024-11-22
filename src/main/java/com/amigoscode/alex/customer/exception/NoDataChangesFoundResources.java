package com.amigoscode.alex.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NoDataChangesFoundResources extends RuntimeException {
    public NoDataChangesFoundResources(String message) {
        super(message);
    }
}

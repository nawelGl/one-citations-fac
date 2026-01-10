package org.gso.citations.exception;

import lombok.Getter;
import org.gso.citations.dto.ErrorMessage;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractBrinderException extends RuntimeException{
    private final transient ErrorMessage errorMessage;
    private final HttpStatus httpStatus;



    protected AbstractBrinderException(HttpStatus httpStatus, ErrorMessage errorMessage) {
        super(errorMessage.message());
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    protected AbstractBrinderException(ErrorMessage errorMessage) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

}

package org.gso.citations.exception;

import org.gso.citations.dto.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends AbstractBrinderException {
    public static final ForbiddenException DEFAULT = new ForbiddenException();

    public static final String FORBIDDEN_CODE = "err.func.brinder.forbidden";
    public static final String FORBIDDEN_MESSAGE = "The access is forbidden";

    private ForbiddenException() {
        super(HttpStatus.FORBIDDEN,
                new ErrorMessage(FORBIDDEN_CODE,FORBIDDEN_MESSAGE));
    }
}

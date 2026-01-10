package org.gso.citations.exception;

import org.gso.citations.dto.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ConflictException extends AbstractBrinderException {
    public static final String CONFLICT_CODE = "err.func.wired.conflict";
    public static final String CONFLICT_MESSAGE =
            "The request could not be completed due to a conflict with the current state of the target resource";

    public static final ConflictException DEFAULT = new ConflictException(CONFLICT_MESSAGE);

    public ConflictException(String message) {
        this(CONFLICT_CODE, message);
    }

    public ConflictException(String code, String message) {
        super(HttpStatus.CONFLICT,
                new ErrorMessage(code, message));
    }
}

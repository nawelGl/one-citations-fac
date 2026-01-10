package org.gso.citations.exception;

import org.gso.citations.dto.ErrorMessage;
import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractBrinderException {
    public static final BadRequestException DEFAULT = new BadRequestException();

    public static final String BAD_REQUEST_CODE = "err.func.brinder.badrequest";
    public static final String BAS_REQUEST_MESSAGE = "The request is bad formated";

    private BadRequestException() {
        super(HttpStatus.BAD_REQUEST,
                new ErrorMessage(BAD_REQUEST_CODE,BAS_REQUEST_MESSAGE));
    }
}

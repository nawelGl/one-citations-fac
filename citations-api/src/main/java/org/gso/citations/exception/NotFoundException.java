package org.gso.citations.exception;

import lombok.Builder;
import lombok.Getter;
import org.gso.citations.dto.ErrorMessage;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class NotFoundException extends AbstractBrinderException {
    public static final NotFoundException DEFAULT = new NotFoundException();

    public static final String NOT_FOUND_CODE = "err.func.brinder.notfound";
    public static final String NOT_FOUND_MESSAGE = "The Ressource is not foud";

    private NotFoundException() {
        super(HttpStatus.NOT_FOUND,
                new ErrorMessage(NOT_FOUND_CODE, NOT_FOUND_MESSAGE));
    }
}

package cubox.aero.skapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED)
public class NoMatchException extends RuntimeException {

    public NoMatchException(String message) {
        super(message);
    }

}

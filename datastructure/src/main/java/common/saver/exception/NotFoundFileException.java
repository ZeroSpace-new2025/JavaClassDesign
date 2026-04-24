package common.saver.exception;

public class NotFoundFileException extends LoadException {
    public NotFoundFileException(String message) {
        super(message);
    }

    public NotFoundFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
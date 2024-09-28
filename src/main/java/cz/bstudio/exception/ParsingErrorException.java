package cz.bstudio.exception;

public class ParsingErrorException extends RuntimeException {
    public ParsingErrorException(String message) {
        super(message);
    }
}